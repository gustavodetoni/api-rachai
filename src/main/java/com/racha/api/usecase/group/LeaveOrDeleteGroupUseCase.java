package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveOrDeleteGroupUseCase {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void execute(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        if (group.getOwner().getId().equals(userId)) {
            group.setDeletedAt(LocalDateTime.now());
            groupRepository.save(group);
        } else {
            GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                    .orElseThrow(() -> new BusinessException("Usuário não é membro deste grupo", HttpStatus.BAD_REQUEST));

            member.setDeletedAt(LocalDateTime.now());
            groupMemberRepository.save(member);
        }
    }
}
