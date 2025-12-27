package com.racha.api.controller;

import com.racha.api.dto.expense.CreateExpenseRequest;
import com.racha.api.dto.expense.ExpenseResponse;
import com.racha.api.usecase.expense.CreateExpenseUseCase;
import com.racha.api.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Despesas", description = "Gerenciamento de despesas")
public class ExpenseController {

    private final CreateExpenseUseCase createExpenseUseCase;
    private final AuthenticationUtil authenticationUtil;

    @PostMapping("/expense/{groupId}")
    @Operation(
            summary = "Criar despesa",
            description = "Cria uma nova despesa no grupo. Se divideTo for vazio, divide entre todos os membros. Caso contrário, divide apenas entre o usuário logado e os IDs fornecidos.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ExpenseResponse> createExpense(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateExpenseRequest request,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        ExpenseResponse response = createExpenseUseCase.execute(groupId, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

