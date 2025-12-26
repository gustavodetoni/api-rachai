package com.racha.api.service;

import com.racha.api.config.auth.JwtTokenProvider;
import com.racha.api.domain.entity.User;
import com.racha.api.expection.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    public String generateAccessToken(User user) {
        return jwtTokenProvider.generateToken(user.getId(), user.getEmail());
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public UUID getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
