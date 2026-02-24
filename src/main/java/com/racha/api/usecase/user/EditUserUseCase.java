package com.racha.api.usecase.user;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.user.EditUserRequest;
import com.racha.api.dto.user.UserResponse;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditUserUseCase {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public UserResponse execute(UUID userId, EditUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND)
                );

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        MultipartFile thumbnail = request.getThumbnail();
        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (!s3Service.isValidImageFile(thumbnail)) {
                throw new BusinessException("Arquivo deve ser uma imagem válida (JPEG, PNG, WEBP)", HttpStatus.BAD_REQUEST);
            }
            String thumbnailUrl = s3Service.uploadFile(thumbnail, "users/thumbnails");
            user.setThumbnail(thumbnailUrl);
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getPixKey() != null) {
            user.setPixKey(request.getPixKey());
        }

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .thumbnail(user.getThumbnail())
                .pixKey(user.getPixKey())
                .build();
    }
}
