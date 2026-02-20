package com.racha.api.controller;

import com.racha.api.dto.expense.CreateExpenseRequest;
import com.racha.api.dto.expense.ExpenseResponse;
import com.racha.api.dto.expense.GroupSummaryResponse;
import com.racha.api.dto.expense.UpdateExpenseSplitRequest;
import com.racha.api.dto.expense.UserDebtsResponse;
import com.racha.api.dto.expense.UserReceivableDto; 
import com.racha.api.domain.enumeration.TransactionType;
import com.racha.api.usecase.expense.CreateExpenseUseCase;
import com.racha.api.usecase.expense.GetGroupSummaryUseCase;
import com.racha.api.usecase.expense.GetUserDebtsUseCase;
import com.racha.api.usecase.expense.GetUserReceivablesUseCase; 
import com.racha.api.usecase.expense.SettleDebtsUseCase;
import com.racha.api.usecase.expense.UpdateExpenseSplitUseCase;
import com.racha.api.usecase.transaction.CreateTransactionUseCase;
import com.racha.api.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; 
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Despesas", description = "Gerenciamento de despesas")
public class ExpenseController {

    private final CreateExpenseUseCase createExpenseUseCase;
    private final GetGroupSummaryUseCase getGroupSummaryUseCase;
    private final GetUserDebtsUseCase getUserDebtsUseCase;
    private final UpdateExpenseSplitUseCase updateExpenseSplitUseCase;
    private final SettleDebtsUseCase settleDebtsUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final AuthenticationUtil authenticationUtil;
    private final GetUserReceivablesUseCase getUserReceivablesUseCase;

    @PostMapping(path = "/expense/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Criar despesa",
            description = "Cria uma nova despesa no grupo. Se divideTo for vazio, divide entre todos os membros. Caso contrário, divide apenas entre o usuário logado e os IDs fornecidos.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ExpenseResponse> createExpense(
            @PathVariable UUID groupId,
            @Valid @ModelAttribute CreateExpenseRequest request,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        ExpenseResponse response = createExpenseUseCase.execute(groupId, request, userId);

        settleDebtsUseCase.execute(groupId);

        createTransactionUseCase.execute(
                groupId,
                userId,
                TransactionType.EXPENSE,
                request.getCategory().toString(),
                request.getTitle(),
                request.getAmount(),
                response.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/expense/{groupId}/summary")
    @Operation(
            summary = "Resumo financeiro do grupo",
            description = "Retorna o total gasto no grupo, total a receber e total a pagar do usuário logado",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<GroupSummaryResponse> getGroupSummary(
            @PathVariable UUID groupId,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        GroupSummaryResponse response = getGroupSummaryUseCase.execute(groupId, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expense/{groupId}/debts")
    @Operation(
            summary = "Dívidas do usuário",
            description = "Retorna a lista de dívidas do usuário logado no grupo",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<UserDebtsResponse> getUserDebts(
            @PathVariable UUID groupId,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        UserDebtsResponse response = getUserDebtsUseCase.execute(groupId, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expense/{groupId}/receivables")
    @Operation(
            summary = "Valores a receber do usuário",
            description = "Retorna a lista de valores que o usuário logado tem a receber de outros membros no grupo.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<UserReceivableDto>> getUserReceivables(
            @PathVariable UUID groupId,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        List<UserReceivableDto> response = getUserReceivablesUseCase.execute(groupId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/expense/split/{expenseSplitId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Atualizar expense split",
            description = "Atualiza o status de pagamento de um expense split. O usuário pode enviar um comprovante opcional.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<Void> updateExpenseSplit(
            @PathVariable UUID expenseSplitId,
            @ModelAttribute UpdateExpenseSplitRequest request,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        updateExpenseSplitUseCase.execute(expenseSplitId, request, userId);

        return ResponseEntity.noContent().build();
    }
}

