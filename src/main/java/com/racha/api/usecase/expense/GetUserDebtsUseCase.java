package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.Group;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.dto.expense.DebtResponse;
import com.racha.api.dto.expense.UserDebtsResponse;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserDebtsUseCase {

    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public UserDebtsResponse execute(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("Usuário não é membro deste grupo", HttpStatus.FORBIDDEN));

        // Buscar todos os expense_splits não pagos do usuário no grupo (com expense e createdBy carregados)
        List<ExpenseSplit> unpaidSplits = expenseSplitRepository.findByUserIdAndGroupIdAndPaidFalseWithExpense(userId, groupId);

        // Agrupar por usuário que deve receber (criador da expense)
        Map<UUID, List<ExpenseSplit>> splitsByCreditor = unpaidSplits.stream()
                .collect(Collectors.groupingBy(split -> split.getExpense().getCreatedBy().getId()));

        // Converter para lista de DebtResponse
        List<DebtResponse> debts = splitsByCreditor.entrySet().stream()
                .map(entry -> {
                    UUID creditorId = entry.getKey();
                    List<ExpenseSplit> splits = entry.getValue();
                    String creditorName = splits.get(0).getExpense().getCreatedBy().getName();
                    String creditorPix = splits.get(0).getExpense().getCreatedBy().getPixKey();
                    
                    BigDecimal totalAmount = splits.stream()
                            .map(ExpenseSplit::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    List<UUID> expenseSplitIds = splits.stream()
                            .map(ExpenseSplit::getId)
                            .collect(Collectors.toList());
                    
                    return DebtResponse.builder()
                            .userId(creditorId)
                            .userName(creditorName)
                            .userPix(creditorPix)
                            .totalAmount(totalAmount)
                            .expenseSplitIds(expenseSplitIds)
                            .build();
                })
                .collect(Collectors.toList());

        return UserDebtsResponse.builder()
                .debts(debts)
                .build();
    }
}

