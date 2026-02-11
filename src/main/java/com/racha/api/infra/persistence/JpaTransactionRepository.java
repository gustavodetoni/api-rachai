package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.Transaction;
import com.racha.api.domain.repository.TransactionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaTransactionRepository extends JpaRepository<Transaction, UUID>, TransactionRepository {
}
