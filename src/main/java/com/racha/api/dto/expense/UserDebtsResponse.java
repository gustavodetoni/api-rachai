package com.racha.api.dto.expense;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDebtsResponse {
    private List<DebtResponse> debts;
}

