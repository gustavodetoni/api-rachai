package com.racha.api.domain.repository;

import com.racha.api.domain.entity.Group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository {
    Group save(Group group);

    Optional<Group> findById(UUID id);

    List<Group> findByOwnerId(UUID ownerId);

    void delete(Group group);
}