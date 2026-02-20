package com.racha.api.domain.repository;

import com.racha.api.domain.entity.GroupInvite;

import java.util.Optional;

public interface GroupInviteRepository {
    GroupInvite save(GroupInvite groupInvite);

    Optional<GroupInvite> findByToken(String token);
}
