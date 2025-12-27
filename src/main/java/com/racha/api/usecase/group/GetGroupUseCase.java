package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.dto.group.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetGroupUseCase {

    private final GroupRepository groupRepository;

    public List<GroupResponse> execute(UUID userId) {
        List<Group> groups = groupRepository.findByUserId(userId);

        return groups.stream()
                .map(group -> GroupResponse.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .description(group.getDescription())
                        .thumbnail(group.getThumbnail())
                        .ownerId(group.getOwner().getId())
                        .ownerName(group.getOwner().getName())
                        .createdAt(group.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
