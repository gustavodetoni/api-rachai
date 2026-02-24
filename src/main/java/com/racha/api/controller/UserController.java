package com.racha.api.controller;

import com.racha.api.dto.user.EditUserRequest;
import com.racha.api.dto.user.UserResponse;
import com.racha.api.usecase.user.EditUserUseCase;
import com.racha.api.usecase.user.GetUserUseCase;
import com.racha.api.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import java.beans.PropertyEditorSupport;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Endpoints de gerenciamento do usuário")
public class UserController {

    private final EditUserUseCase editUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final AuthenticationUtil authenticationUtil;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(MultipartFile.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(null);
            }
        });
    }

    @GetMapping("/user")
    @Operation(
            summary = "Buscar usuário",
            description = "Informações sobre meu usuário",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<UserResponse> getUser(HttpServletRequest request) {
        UUID userId = authenticationUtil.getUserIdFromRequest(request);
        UserResponse user = getUserUseCase.execute(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping(path = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Editar usuário",
            description = "Edita as informações do usuário autenticado",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<UserResponse> editUser(
            HttpServletRequest request,
            @ModelAttribute EditUserRequest editRequest
    ) {
        UUID userId = authenticationUtil.getUserIdFromRequest(request);
        UserResponse user = editUserUseCase.execute(userId, editRequest);
        return ResponseEntity.ok(user);
    }
}
