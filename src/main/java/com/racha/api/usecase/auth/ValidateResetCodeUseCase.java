package com.racha.api.usecase.auth;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.auth.ValidateResetCodeRequest;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ValidateResetCodeUseCase {

    private final UserRepository userRepository;

    public void execute(ValidateResetCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email não encontrado", HttpStatus.NOT_FOUND));

        if (user.getResetCode() == null || !user.getResetCode().equals(request.getCode())) {
            throw new BusinessException("Código inválido", HttpStatus.BAD_REQUEST);
        }

        if (user.getResetCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Código expirado. Solicite um novo código", HttpStatus.BAD_REQUEST);
        }
    }
}
