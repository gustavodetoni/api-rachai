package com.racha.api.controller;

import com.racha.api.dto.transaction.TransactionDetailResponse;
import com.racha.api.dto.transaction.TransactionResponse;
import com.racha.api.usecase.transaction.GetTransactionDetailUseCase;
import com.racha.api.usecase.transaction.GetTransactionsByGroupUseCase;
import com.racha.api.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Gerenciamento de transações")
public class TransactionController {

    private final AuthenticationUtil authenticationUtil;
    private final GetTransactionsByGroupUseCase getTransactionsByGroupUseCase;
    private final GetTransactionDetailUseCase getTransactionDetailUseCase;

    @GetMapping("/transaction/{groupId}")
    @Operation(
            summary = "Listar transações do grupo",
            description = "Lista as transações de um grupo, aplicando filtros de visibilidade baseado no tipo de transação e usuário logado.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<TransactionResponse>> getTransactionsByGroup(
            @PathVariable UUID groupId,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        List<TransactionResponse> response = getTransactionsByGroupUseCase.execute(groupId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction/detail/{transactionId}")
    @Operation(
            summary = "Detalhes da transação",
            description = "Retorna os detalhes completos de uma transação, incluindo divisões de despesas e comprovantes se houver.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<TransactionDetailResponse> getTransactionDetail(
            @PathVariable UUID transactionId,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        TransactionDetailResponse response = getTransactionDetailUseCase.execute(transactionId, userId);
        return ResponseEntity.ok(response);
    }
}
