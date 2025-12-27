package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.repository.GroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaGroupRepository extends JpaRepository<Group, UUID>, GroupRepository {

    List<Group> findByOwnerId(UUID ownerId);
}