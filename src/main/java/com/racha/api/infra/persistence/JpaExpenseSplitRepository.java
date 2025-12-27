package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID>, ExpenseSplitRepository {
}

