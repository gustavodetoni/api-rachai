package com.racha.api.domain.repository;

import com.racha.api.domain.entity.GroupInvite;

import java.util.Optional;
import java.util.UUID;

public interface GroupInviteRepository {
    GroupInvite save(GroupInvite groupInvite);

    Optional<GroupInvite> findByToken(String token);

    Optional<GroupInvite> findLatestByGroupId(UUID groupId);
}
