package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.GroupInvite;
import com.racha.api.domain.repository.GroupInviteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaGroupInviteRepository extends JpaRepository<GroupInvite, UUID>, GroupInviteRepository {
    Optional<GroupInvite> findByToken(String token);
}
