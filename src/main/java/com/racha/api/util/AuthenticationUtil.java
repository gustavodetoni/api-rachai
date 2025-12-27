package com.racha.api.util;

import com.racha.api.config.auth.JwtTokenProvider;
import com.racha.api.expection.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public UUID getUserIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new BusinessException("Token não encontrado", HttpStatus.UNAUTHORIZED);
    }
}