package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.GroupInvite;
import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.GroupInviteRepository;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinGroupByInviteUseCase {

    private final GroupInviteRepository groupInviteRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(String token, UUID userId) {
        GroupInvite invite = groupInviteRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Convite inválido ou não encontrado", HttpStatus.NOT_FOUND));

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Convite expirado", HttpStatus.BAD_REQUEST);
        }

        Group group = invite.getGroup();
        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado ou deletado", HttpStatus.NOT_FOUND);
        }

        Optional<GroupMember> existingMemberOpt = groupMemberRepository.findByGroupIdAndUserIdIncludeDeleted(group.getId(), userId);

        if (existingMemberOpt.isPresent()) {
            GroupMember existingMember = existingMemberOpt.get();
            if (existingMember.getDeletedAt() == null) {
                throw new BusinessException("Usuário já é membro deste grupo", HttpStatus.CONFLICT);
            }
            // Reativar membro deletado
            existingMember.setDeletedAt(null);
            groupMemberRepository.save(existingMember);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

            GroupMember newMember = GroupMember.builder()
                    .group(group)
                    .user(user)
                    .build();

            groupMemberRepository.save(newMember);
        }
    }
}
