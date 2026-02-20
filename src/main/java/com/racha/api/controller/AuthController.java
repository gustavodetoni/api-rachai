package com.racha.api.controller;

import com.racha.api.dto.auth.LoginRequest;
import com.racha.api.dto.auth.LoginResponse;
import com.racha.api.dto.auth.MessageResponse;
import com.racha.api.dto.auth.ProvisionalResetPasswordRequest;
import com.racha.api.dto.auth.RegisterRequest;
import com.racha.api.dto.auth.ResetPasswordConfirmRequest;
import com.racha.api.dto.auth.ResetPasswordRequest;
import com.racha.api.dto.auth.ValidateResetCodeRequest;
import com.racha.api.usecase.auth.ConfirmPasswordResetUseCase;
import com.racha.api.usecase.auth.LoginUseCase;
import com.racha.api.usecase.auth.ProvisionalResetPasswordUseCase;
import com.racha.api.usecase.auth.RegisterUseCase;
import com.racha.api.usecase.auth.RequestPasswordResetUseCase;
import com.racha.api.usecase.auth.ValidateResetCodeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e gerenciamento de senha")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ValidateResetCodeUseCase validateResetCodeUseCase;
    private final ConfirmPasswordResetUseCase confirmPasswordResetUseCase;
    private final ProvisionalResetPasswordUseCase provisionalResetPasswordUseCase;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna tokens de acesso")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = registerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/reset-password/request")
    @Operation(summary = "Solicitar código de reset", description = "Envia email com código de 6 dígitos para reset de senha")
    public ResponseEntity<MessageResponse> requestPasswordReset(@Valid @RequestBody ResetPasswordRequest request) {
        requestPasswordResetUseCase.execute(request);
        return ResponseEntity.ok(new MessageResponse("Código foi enviado"));
    }

    @PostMapping("/reset-password/validate")
    @Operation(summary = "Validar código de reset", description = "Valida o código de 6 dígitos recebido por email")
    public ResponseEntity<MessageResponse> validateResetCode(@Valid @RequestBody ValidateResetCodeRequest request) {
        validateResetCodeUseCase.execute(request);
        return ResponseEntity.ok(new MessageResponse("Código validado com sucesso"));
    }

    @PostMapping("/reset-password/confirm")
    @Operation(summary = "Confirmar reset de senha", description = "Redefine a senha usando o código validado")
    public ResponseEntity<MessageResponse> confirmPasswordReset(@Valid @RequestBody ResetPasswordConfirmRequest request) {
        confirmPasswordResetUseCase.execute(request);
        return ResponseEntity.ok(new MessageResponse("Senha redefinida com sucesso"));
    }

    @PostMapping("/reset-password/provisor")
    @Operation(summary = "Reset de senha provisório", description = "Redefine a senha diretamente usando email e nova senha (USO INTERNO)")
    public ResponseEntity<MessageResponse> provisionalResetPassword(@Valid @RequestBody ProvisionalResetPasswordRequest request) {
        provisionalResetPasswordUseCase.execute(request);
        return ResponseEntity.ok(new MessageResponse("Senha redefinida com sucesso"));
    }
}