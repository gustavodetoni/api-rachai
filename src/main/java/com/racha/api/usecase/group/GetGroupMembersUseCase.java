package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.dto.user.UserResponse;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetGroupMembersUseCase {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> execute(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(groupId);

        return groupMembers.stream()
                .filter(groupMember ->
                        !groupMember.getUser().getId().equals(userId)
                )
                .map(groupMember -> {
                    User user = groupMember.getUser();
                    return UserResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .thumbnail(user.getThumbnail())
                            .pixKey(user.getPixKey())
                            .build();
                })
                .toList();
    }
}
