package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.Expense;
import com.racha.api.domain.repository.ExpenseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaExpenseRepository extends JpaRepository<Expense, UUID>, ExpenseRepository {

    @Override
    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId AND e.deletedAt IS NULL")
    List<Expense> findByGroupId(@Param("groupId") UUID groupId);
}

