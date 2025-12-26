package com.racha.api.domain.repository;

import com.racha.api.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
    boolean existsByEmail(String email);
    Optional<User> findByResetCode(String resetCode);
}
