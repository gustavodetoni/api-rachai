package com.racha.api.dto.expense;

import com.racha.api.domain.enumeration.CategoryExpense;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpenseRequest {

    @NotBlank(message = "Título da despesa é obrigatório")
    private String title;

    @NotNull(message = "Valor da despesa é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;

    private CategoryExpense category;

    private List<UUID> divideTo;

    private MultipartFile invoice;
}

