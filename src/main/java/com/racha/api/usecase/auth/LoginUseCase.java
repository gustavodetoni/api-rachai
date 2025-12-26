package com.racha.api.usecase.auth;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.auth.LoginRequest;
import com.racha.api.dto.auth.LoginResponse;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.PasswordService;
import com.racha.api.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public LoginResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED));

        if (!passwordService.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateAccessToken(user);

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }
}
