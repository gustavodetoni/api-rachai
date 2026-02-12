package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.dto.expense.UserReceivableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserReceivablesUseCase {

    private final ExpenseSplitRepository expenseSplitRepository;

    public List<UserReceivableDto> execute(UUID groupId, UUID authenticatedUserId) {
        List<ExpenseSplit> receivables = expenseSplitRepository.findByGroupIdAndExpenseCreatedByAndPaidFalse(groupId, authenticatedUserId);

        Map<UUID, BigDecimal> receivablesByPayer = receivables.stream()
                .collect(Collectors.groupingBy(
                        es -> es.getUser().getId(),
                        Collectors.mapping(ExpenseSplit::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return receivablesByPayer.entrySet().stream()
                .map(entry -> {
                    UUID payerId = entry.getKey();
                    BigDecimal totalAmount = entry.getValue();

                    ExpenseSplit exampleExpenseSplit = receivables.stream()
                            .filter(es -> es.getUser().getId().equals(payerId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Payer not found in receivables after grouping."));

                    return UserReceivableDto.builder()
                            .payerId(payerId)
                            .payerName(exampleExpenseSplit.getUser().getName())
                            .payerThumbnailUrl(exampleExpenseSplit.getUser().getThumbnail()) // Assuming User entity has getThumbnail()
                            .amount(totalAmount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
