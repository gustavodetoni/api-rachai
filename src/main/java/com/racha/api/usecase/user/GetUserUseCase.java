package com.racha.api.usecase.user;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;

    public UserResponse execute(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> UserResponse.builder()
                .id(value.getId())
                .name(value.getName())
                .thumbnail(value.getThumbnail())
                .pixKey(value.getPixKey())
                .build()).orElse(null);
    }
}
