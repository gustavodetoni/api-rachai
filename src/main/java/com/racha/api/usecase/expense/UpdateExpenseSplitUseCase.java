package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.enumeration.TransactionType;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.expense.UpdateExpenseSplitRequest;
import com.racha.api.expection.BusinessException;
import com.racha.api.usecase.transaction.CreateTransactionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateExpenseSplitUseCase {

    private final ExpenseSplitRepository expenseSplitRepository;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UserRepository userRepository;

    @Transactional
    public ExpenseSplit execute(UUID expenseSplitId, UpdateExpenseSplitRequest request, UUID userId) {
        ExpenseSplit expenseSplit = expenseSplitRepository.findByIdWithUser(expenseSplitId)
                .orElseThrow(() -> new BusinessException("Expense split não encontrado", HttpStatus.NOT_FOUND));

        if (expenseSplit.getDeletedAt() != null) {
            throw new BusinessException("Expense split não encontrado", HttpStatus.NOT_FOUND);
        }

        if (!expenseSplit.getUser().getId().equals(userId)) {
            throw new BusinessException("Usuário não tem permissão para editar este expense split", HttpStatus.FORBIDDEN);
        }

        expenseSplit.setPaid(request.getPaid());
        
        if (request.getPaid()) {
            expenseSplit.setPaidAt(LocalDateTime.now());
            expenseSplitRepository.save(expenseSplit);

            User payer = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException("Payer user not found", HttpStatus.NOT_FOUND));
            User receiver = expenseSplit.getExpense().getCreatedBy();

            createTransactionUseCase.execute(
                    expenseSplit.getExpense().getGroup().getId(),
                    payer.getId(),
                    TransactionType.PAYMENT,
                    "Você pagou",
                    receiver.getName(),
                    expenseSplit.getAmount()
            );

            createTransactionUseCase.execute(
                    expenseSplit.getExpense().getGroup().getId(),
                    receiver.getId(),
                    TransactionType.RECEIVE,
                    "Você recebeu",
                    payer.getName(),
                    expenseSplit.getAmount()
            );

        } else {
            expenseSplit.setPaidAt(null);
            expenseSplitRepository.save(expenseSplit);
        }
        return expenseSplit;
    }
}

