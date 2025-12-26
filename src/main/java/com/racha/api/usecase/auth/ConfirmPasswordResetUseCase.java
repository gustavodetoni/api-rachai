package com.racha.api.usecase.auth;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.auth.ResetPasswordConfirmRequest;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfirmPasswordResetUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public void execute(ResetPasswordConfirmRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email não encontrado", HttpStatus.NOT_FOUND));

        if (user.getResetCode() == null || !user.getResetCode().equals(request.getCode())) {
            throw new BusinessException("Código inválido", HttpStatus.BAD_REQUEST);
        }

        if (user.getResetCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Código expirado. Solicite um novo código", HttpStatus.BAD_REQUEST);
        }

        String hashedPassword = passwordService.hashPassword(request.getNewPassword());
        user.setPassword(hashedPassword);
        user.setResetCode(null);
        user.setResetCodeExpiresAt(null);

        userRepository.save(user);
    }
}