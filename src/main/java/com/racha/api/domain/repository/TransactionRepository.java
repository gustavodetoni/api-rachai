package com.racha.api.domain.repository;

import com.racha.api.domain.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findByGroupId(UUID groupId);
}
