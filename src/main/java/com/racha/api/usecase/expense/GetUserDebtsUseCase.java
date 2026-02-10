package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.entity.User;
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
import java.util.ArrayList;
import java.util.HashMap;
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

    private record Balance(User user, BigDecimal amount) {}
    private record SimplifiedDebt(User debtor, User creditor, BigDecimal amount) {}

    @Transactional(readOnly = true)
    public UserDebtsResponse execute(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("Usuário não é membro deste grupo", HttpStatus.FORBIDDEN));

        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        Map<UUID, User> userMap = members.stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<User, BigDecimal> balances = new HashMap<>();
        for (User user : userMap.values()) {
            balances.put(user, BigDecimal.ZERO);
        }

        List<ExpenseSplit> splits = expenseSplitRepository.findByGroupIdAndPaidFalse(groupId);

        for (ExpenseSplit split : splits) {
            User payer = split.getExpense().getCreatedBy();
            User ower = split.getUser();
            BigDecimal amount = split.getAmount();

            if (!payer.equals(ower)) {
                balances.merge(payer, amount, BigDecimal::add);
                balances.merge(ower, amount.negate(), BigDecimal::add);
            }
        }

        List<Balance> creditors = new ArrayList<>();
        List<Balance> debtors = new ArrayList<>();

        for (Map.Entry<User, BigDecimal> entry : balances.entrySet()) {
            int comparison = entry.getValue().compareTo(BigDecimal.ZERO);
            if (comparison > 0) {
                creditors.add(new Balance(entry.getKey(), entry.getValue()));
            } else if (comparison < 0) {
                debtors.add(new Balance(entry.getKey(), entry.getValue().abs()));
            }
        }

        List<SimplifiedDebt> simplifiedDebts = new ArrayList<>();
        int creditorIndex = 0;
        int debtorIndex = 0;

        while (creditorIndex < creditors.size() && debtorIndex < debtors.size()) {
            Balance creditor = creditors.get(creditorIndex);
            Balance debtor = debtors.get(debtorIndex);

            BigDecimal payment = creditor.amount.min(debtor.amount);

            simplifiedDebts.add(new SimplifiedDebt(debtor.user, creditor.user, payment));

            BigDecimal remainingCreditor = creditor.amount.subtract(payment);
            BigDecimal remainingDebtor = debtor.amount.subtract(payment);

            creditors.set(creditorIndex, new Balance(creditor.user, remainingCreditor));
            debtors.set(debtorIndex, new Balance(debtor.user, remainingDebtor));

            if (remainingCreditor.compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }
            if (remainingDebtor.compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }
        }

        List<DebtResponse> userDebts = simplifiedDebts.stream()
                .filter(debt -> debt.debtor.getId().equals(userId))
                .map(debt -> DebtResponse.builder()
                        .userId(debt.creditor.getId())
                        .userName(debt.creditor.getName())
                        .userPix(debt.creditor.getPixKey())
                        .totalAmount(debt.amount)
                        .build())
                .collect(Collectors.toList());

        return UserDebtsResponse.builder()
                .debts(userDebts)
                .build();
    }
}