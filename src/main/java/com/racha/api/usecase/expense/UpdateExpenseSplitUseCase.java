package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.enumeration.TransactionType;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.expense.UpdateExpenseSplitRequest;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.S3Service;
import com.racha.api.usecase.transaction.CreateTransactionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateExpenseSplitUseCase {

    private final ExpenseSplitRepository expenseSplitRepository;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UserRepository userRepository;
    private final S3Service s3Service;

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

        if (expenseSplit.getPaid() == Boolean.TRUE) {
            throw new BusinessException("Este pagamento já foi realizado", HttpStatus.BAD_REQUEST);
        }

        String evidenceUrl = null;
        MultipartFile evidence = request.getEvidence();
        if (evidence != null && !evidence.isEmpty()) {
            if (!s3Service.isValidDocumentFile(evidence)) {
                throw new BusinessException("Arquivo deve ser uma imagem válida ou PDF", HttpStatus.BAD_REQUEST);
            }
            evidenceUrl = s3Service.uploadFile(evidence, "expenses/evidences");
        }

        expenseSplit.setPaid(true);
        expenseSplit.setPaidAt(LocalDateTime.now());
        expenseSplit.setEvidence(evidenceUrl);
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
                expenseSplit.getAmount(),
                expenseSplit.getExpense().getId()
        );

        createTransactionUseCase.execute(
                expenseSplit.getExpense().getGroup().getId(),
                receiver.getId(),
                TransactionType.RECEIVE,
                "Você recebeu",
                payer.getName(),
                expenseSplit.getAmount(),
                expenseSplit.getExpense().getId()
        );

        return expenseSplit;
    }
}

