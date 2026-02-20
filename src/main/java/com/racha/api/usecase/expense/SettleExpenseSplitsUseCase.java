package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.enumeration.TransactionType;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.expense.SettleExpenseSplitsRequest;
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
public class SettleExpenseSplitsUseCase {

    private final ExpenseSplitRepository expenseSplitRepository;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public void execute(SettleExpenseSplitsRequest request, UUID userId) {
        if (request.getExpenseSplitIds() == null || request.getExpenseSplitIds().isEmpty()) {
            throw new BusinessException("Pelo menos um ID de dívida deve ser fornecido", HttpStatus.BAD_REQUEST);
        }

        User payer = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Payer user not found", HttpStatus.NOT_FOUND));

        String evidenceUrl = null;
        MultipartFile evidence = request.getEvidence();
        if (evidence != null && !evidence.isEmpty()) {
            if (!s3Service.isValidDocumentFile(evidence)) {
                throw new BusinessException("Arquivo deve ser uma imagem válida ou PDF", HttpStatus.BAD_REQUEST);
            }
            evidenceUrl = s3Service.uploadFile(evidence, "expenses/evidences");
        }

        for (UUID expenseSplitId : request.getExpenseSplitIds()) {
            ExpenseSplit expenseSplit = expenseSplitRepository.findByIdWithUser(expenseSplitId)
                    .orElseThrow(() -> new BusinessException("Dívida não encontrada: " + expenseSplitId, HttpStatus.NOT_FOUND));

            if (expenseSplit.getDeletedAt() != null) {
                throw new BusinessException("Dívida não encontrada: " + expenseSplitId, HttpStatus.NOT_FOUND);
            }

            if (!expenseSplit.getUser().getId().equals(userId)) {
                throw new BusinessException("Usuário não tem permissão para pagar esta dívida: " + expenseSplitId, HttpStatus.FORBIDDEN);
            }

            if (expenseSplit.getPaid()) {
                throw new BusinessException("Esta dívida já foi paga: " + expenseSplitId, HttpStatus.BAD_REQUEST);
            }

            expenseSplit.setPaid(true);
            expenseSplit.setPaidAt(LocalDateTime.now());
            expenseSplit.setEvidence(evidenceUrl);
            expenseSplitRepository.save(expenseSplit);

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
        }
    }
}
