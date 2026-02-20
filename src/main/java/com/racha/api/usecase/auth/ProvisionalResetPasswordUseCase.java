package com.racha.api.usecase.auth;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.auth.ProvisionalResetPasswordRequest;
import com.racha.api.expection.BusinessException;
import com.racha.api.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProvisionalResetPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public void execute(ProvisionalResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email não encontrado", HttpStatus.NOT_FOUND));

        String hashedPassword = passwordService.hashPassword(request.getNewPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);
    }
}
