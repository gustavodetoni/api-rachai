package com.racha.api.usecase.user;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.user.EditUserRequest;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditUserUseCase {

    private final UserRepository userRepository;

    public User execute(UUID userId, EditUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND)
                );

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.thumbnail() != null) {
            user.setThumbnail(request.thumbnail());
        }

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.pixKey() != null) {
            user.setPixKey(request.pixKey());
        }

        return userRepository.save(user);
    }
}
