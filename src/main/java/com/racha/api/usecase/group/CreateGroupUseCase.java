package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.group.CreateGroupRequest;
import com.racha.api.dto.group.GroupResponse;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateGroupUseCase {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public GroupResponse execute(CreateGroupRequest request, MultipartFile thumbnail, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        String thumbnailUrl = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (!s3Service.isValidImageFile(thumbnail)) {
                throw new BusinessException("Arquivo deve ser uma imagem válida (JPEG, PNG, WEBP)", HttpStatus.BAD_REQUEST);
            }
            thumbnailUrl = s3Service.uploadFile(thumbnail, "groups/thumbnails");
        }

        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .thumbnail(thumbnailUrl)
                .owner(user)
                .build();

        group = groupRepository.save(group);

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .thumbnail(group.getThumbnail())
                .ownerId(user.getId())
                .ownerName(user.getName())
                .createdAt(group.getCreatedAt())
                .build();
    }
}