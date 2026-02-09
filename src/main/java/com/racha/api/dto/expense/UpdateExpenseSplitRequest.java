package com.racha.api.dto.expense;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpenseSplitRequest {
    @NotNull(message = "Campo paid é obrigatório")
    private Boolean paid;
}

