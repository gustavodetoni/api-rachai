package com.racha.api.usecase.auth;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.auth.LoginResponse;
import com.racha.api.dto.auth.RegisterRequest;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.PasswordService;
import com.racha.api.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public LoginResponse execute(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado", HttpStatus.BAD_REQUEST);
        }

        String hashedPassword = passwordService.hashPassword(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .pixKey(request.getPixKey())
                .build();

        user = userRepository.save(user);

        String token = tokenService.generateAccessToken(user);

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
