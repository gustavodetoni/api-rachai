package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {

    @Override
    Optional<User> findByEmail(String email);

    @Override
    Optional<User> findById(UUID id);

    @Override
    boolean existsByEmail(String email);

    @Override
    Optional<User> findByResetCode(String token);
}
