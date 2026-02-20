package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.GroupInvite;
import com.racha.api.domain.repository.GroupInviteRepository;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class GenerateGroupInviteUseCase {

    private final GroupRepository groupRepository;
    private final GroupInviteRepository groupInviteRepository;

    @Transactional
    public String execute(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        String token = generateNumericToken(8);

        GroupInvite invite = GroupInvite.builder()
                .group(group)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        groupInviteRepository.save(invite);

        return token;
    }

    private String generateNumericToken(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }
}
