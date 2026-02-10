package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID>, ExpenseSplitRepository {

    @Override
    @Query("SELECT es FROM ExpenseSplit es JOIN es.expense e WHERE es.user.id = :userId AND e.group.id = :groupId AND es.deletedAt IS NULL AND e.deletedAt IS NULL")
    List<ExpenseSplit> findByUserIdAndGroupId(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

    @Override
    @Query("SELECT es FROM ExpenseSplit es JOIN es.expense e WHERE es.user.id = :userId AND e.group.id = :groupId AND es.paid = false AND es.deletedAt IS NULL AND e.deletedAt IS NULL")
    List<ExpenseSplit> findByUserIdAndGroupIdAndPaidFalse(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

    @Override
    @Query("SELECT es FROM ExpenseSplit es WHERE es.expense.id = :expenseId AND es.deletedAt IS NULL")
    List<ExpenseSplit> findByExpenseId(@Param("expenseId") UUID expenseId);

    @Override
    @Query("SELECT es FROM ExpenseSplit es JOIN es.expense e WHERE e.group.id = :groupId AND e.createdBy.id = :createdByUserId AND es.user.id != :createdByUserId AND es.paid = false AND es.deletedAt IS NULL AND e.deletedAt IS NULL")
    List<ExpenseSplit> findByGroupIdAndExpenseCreatedByAndPaidFalse(@Param("groupId") UUID groupId, @Param("createdByUserId") UUID createdByUserId);

    @Override
    @Query("SELECT es FROM ExpenseSplit es JOIN FETCH es.expense e JOIN FETCH e.createdBy WHERE es.user.id = :userId AND e.group.id = :groupId AND es.paid = false AND es.deletedAt IS NULL AND e.deletedAt IS NULL")
    List<ExpenseSplit> findByUserIdAndGroupIdAndPaidFalseWithExpense(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

    @Override
    @Query("SELECT es FROM ExpenseSplit es JOIN FETCH es.expense e JOIN FETCH e.createdBy JOIN FETCH es.user WHERE e.group.id = :groupId AND es.paid = false AND es.deletedAt IS NULL AND e.deletedAt IS NULL")
    List<ExpenseSplit> findByGroupIdAndPaidFalse(@Param("groupId") UUID groupId);

    @Override
    @Query("SELECT es FROM ExpenseSplit es JOIN es.expense e WHERE e.group.id = :groupId AND es.user.id = :debtorId AND e.createdBy.id = :creditorId AND es.paid = false AND es.deletedAt IS NULL AND e.deletedAt IS NULL")
    List<ExpenseSplit> findUnpaidSplitsBetweenUsers(@Param("groupId") UUID groupId, @Param("debtorId") UUID debtorId, @Param("creditorId") UUID creditorId);

    @Override
    @Query("SELECT es FROM ExpenseSplit es JOIN FETCH es.user WHERE es.id = :id AND es.deletedAt IS NULL")
    Optional<ExpenseSplit> findByIdWithUser(@Param("id") UUID id);
}

