package com.racha.api.controller;

import com.racha.api.config.auth.JwtTokenProvider;
import com.racha.api.domain.entity.User;
import com.racha.api.dto.user.EditUserRequest;
import com.racha.api.expection.BusinessException;
import com.racha.api.usecase.user.EditUserUseCase;
import com.racha.api.usecase.user.GetUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Endpoints de gerenciamento do usuário")
public class UserController {

    private final EditUserUseCase editUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/users")
    @Operation(
            summary = "Buscar usuário",
            description = "Informações sobre meu usuário",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);
        Optional<User> user = getUserUseCase.execute(userId);
        return ResponseEntity.ok(user.orElse(null));
    }

    @PutMapping("/user")
    @Operation(
            summary = "Editar usuário",
            description = "Edita as informações do usuário autenticado",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<User> editUser(
            HttpServletRequest request,
            @RequestBody EditUserRequest editRequest
    ) {
        UUID userId = getUserIdFromToken(request);
        User user = editUserUseCase.execute(userId, editRequest);
        return ResponseEntity.ok(user);
    }

    private UUID getUserIdFromToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new BusinessException("Token não encontrado", HttpStatus.UNAUTHORIZED);
    }
}
