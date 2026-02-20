package com.racha.api.domain.repository;

import com.racha.api.domain.entity.GroupMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository {
    GroupMember save(GroupMember groupMember);

    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);

    List<GroupMember> findByGroupId(UUID groupId);

    Optional<GroupMember> findByGroupIdAndUserIdIncludeDeleted(UUID groupId, UUID userId);

    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
}
