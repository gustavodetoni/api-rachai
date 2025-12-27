package com.racha.api.infra.persistence;

import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.repository.GroupMemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaGroupMemberRepository extends JpaRepository<GroupMember, UUID>, GroupMemberRepository {
}
