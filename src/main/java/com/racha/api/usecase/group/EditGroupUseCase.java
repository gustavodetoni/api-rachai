package com.racha.api.usecase.group;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.dto.group.EditGroupRequest;
import com.racha.api.dto.group.GroupResponse;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditGroupUseCase {

    private final GroupRepository groupRepository;
    private final S3Service s3Service;

    @Transactional
    public GroupResponse execute(UUID groupId, EditGroupRequest request, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        if (!group.getOwner().getId().equals(userId)) {
            throw new BusinessException("Apenas o dono do grupo pode editá-lo", HttpStatus.FORBIDDEN);
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            group.setName(request.getName());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            group.setDescription(request.getDescription());
        }

        MultipartFile thumbnail = request.getThumbnail();
        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (!s3Service.isValidImageFile(thumbnail)) {
                throw new BusinessException("Arquivo deve ser uma imagem válida (JPEG, PNG, WEBP)", HttpStatus.BAD_REQUEST);
            }

            // Opcional: Deletar a imagem antiga do S3 se existir
            // if (group.getThumbnail() != null) {
            //     s3Service.deleteFile(group.getThumbnail());
            // }

            String thumbnailUrl = s3Service.uploadFile(thumbnail, "groups/thumbnails");
            group.setThumbnail(thumbnailUrl);
        }

        group = groupRepository.save(group);

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .thumbnail(group.getThumbnail())
                .ownerId(group.getOwner().getId())
                .ownerName(group.getOwner().getName())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
