package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.repository.GroupMemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaGroupMemberRepository extends JpaRepository<GroupMember, UUID>, GroupMemberRepository {

    @Override
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.user.id = :userId AND gm.deletedAt IS NULL")
    Optional<GroupMember> findByGroupIdAndUserId(@Param("groupId") UUID groupId, @Param("userId") UUID userId);

    @Override
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.deletedAt IS NULL")
    List<GroupMember> findByGroupId(@Param("groupId") UUID groupId);

    @Override
    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.user.id = :userId AND gm.deletedAt IS NULL")
    boolean existsByGroupIdAndUserId(@Param("groupId") UUID groupId, @Param("userId") UUID userId);
}
