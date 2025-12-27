package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.Expense;
import com.racha.api.domain.repository.ExpenseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaExpenseRepository extends JpaRepository<Expense, UUID>, ExpenseRepository {
}

