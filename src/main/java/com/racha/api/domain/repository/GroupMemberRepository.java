package com.racha.api.domain.repository;

import com.racha.api.domain.entity.GroupMember;

public interface GroupMemberRepository {
    GroupMember save(GroupMember groupMember);
}
