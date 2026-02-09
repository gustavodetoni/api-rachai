package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.dto.expense.UpdateExpenseSplitRequest;
import com.racha.api.expection.BusinessException;
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

    @Transactional
    public void execute(UUID expenseSplitId, UpdateExpenseSplitRequest request, UUID userId) {
        ExpenseSplit expenseSplit = expenseSplitRepository.findById(expenseSplitId)
                .orElseThrow(() -> new BusinessException("Expense split não encontrado", HttpStatus.NOT_FOUND));

        if (expenseSplit.getDeletedAt() != null) {
            throw new BusinessException("Expense split não encontrado", HttpStatus.NOT_FOUND);
        }

        // Verificar se o usuário é o dono do expense split
        if (!expenseSplit.getUser().getId().equals(userId)) {
            throw new BusinessException("Usuário não tem permissão para editar este expense split", HttpStatus.FORBIDDEN);
        }

        expenseSplit.setPaid(request.getPaid());
        
        // Se foi marcado como pago, definir paidAt
        if (request.getPaid()) {
            expenseSplit.setPaidAt(LocalDateTime.now());
        } else {
            expenseSplit.setPaidAt(null);
        }

        expenseSplitRepository.save(expenseSplit);
    }
}

