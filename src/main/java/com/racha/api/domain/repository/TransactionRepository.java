package com.racha.api.domain.repository;

import com.racha.api.domain.entity.Transaction;

import java.util.List;
import java.util.UUID;

import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findByGroupIdOrderByCreatedAtDesc(UUID groupId);
    Optional<Transaction> findById(UUID id);
}
