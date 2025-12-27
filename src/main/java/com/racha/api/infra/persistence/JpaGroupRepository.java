package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.repository.GroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaGroupRepository extends JpaRepository<Group, UUID>, GroupRepository {

    List<Group> findByOwnerId(UUID ownerId);

    @Query("SELECT DISTINCT g FROM Group g JOIN FETCH g.owner WHERE g.id IN (SELECT gm.group.id FROM GroupMember gm WHERE gm.user.id = :userId AND gm.deletedAt IS NULL) AND g.deletedAt IS NULL")
    List<Group> findByUserId(@Param("userId") UUID userId);
}