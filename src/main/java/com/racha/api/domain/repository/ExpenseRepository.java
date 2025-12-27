package com.racha.api.domain.repository;

import com.racha.api.domain.entity.Expense;

import java.util.UUID;

public interface ExpenseRepository {
    Expense save(Expense expense);
}

