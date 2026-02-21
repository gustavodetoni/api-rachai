package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.repository.GroupInviteRepository;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.dto.auth.MessageResponse;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetGroupInviteUseCase {

    private final GroupRepository groupRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final GroupMemberRepository groupMemberRepository;

    public MessageResponse execute(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new BusinessException("Usuário não é membro deste grupo", HttpStatus.FORBIDDEN);
        }

        return groupInviteRepository.findLatestByGroupId(groupId)
                .filter(invite -> invite.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(invite -> new MessageResponse(invite.getToken()))
                .orElse(new MessageResponse(null));
    }
}
