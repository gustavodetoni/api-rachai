package com.racha.api.usecase.auth;

import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.auth.ResetPasswordRequest;
import com.racha.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RequestPasswordResetUseCase {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public void execute(ResetPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        // Não revela se o email existe ou não (segurança)
        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        // Gera código de 6 dígitos
        String resetCode = generateSixDigitCode();
        user.setResetCode(resetCode);
        user.setResetCodeExpiresAt(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        // Envia email
        emailService.sendPasswordResetEmail(user.getEmail(), resetCode);
    }

    private String generateSixDigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}