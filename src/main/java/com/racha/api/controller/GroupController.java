package com.racha.api.controller;

import com.racha.api.dto.auth.MessageResponse;
import com.racha.api.dto.group.CreateGroupRequest;
import com.racha.api.dto.group.EditGroupRequest;
import com.racha.api.dto.group.GroupResponse;
import com.racha.api.dto.group.JoinGroupResponse;
import com.racha.api.dto.user.UserResponse;
import com.racha.api.usecase.group.CreateGroupUseCase;
import com.racha.api.usecase.group.EditGroupUseCase;
import com.racha.api.usecase.group.GenerateGroupInviteUseCase;
import com.racha.api.usecase.group.GetGroupInviteUseCase;
import com.racha.api.usecase.group.GetGroupMembersUseCase;
import com.racha.api.usecase.group.GetGroupUseCase;
import com.racha.api.usecase.group.JoinGroupByInviteUseCase;
import com.racha.api.usecase.group.LeaveOrDeleteGroupUseCase;
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

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Grupos", description = "Gerenciamento de grupos")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final EditGroupUseCase editGroupUseCase;
    private final GetGroupUseCase getGroupUseCase;
    private final GetGroupMembersUseCase getGroupMembersUseCase;
    private final LeaveOrDeleteGroupUseCase leaveOrDeleteGroupUseCase;
    private final GenerateGroupInviteUseCase generateGroupInviteUseCase;
    private final GetGroupInviteUseCase getGroupInviteUseCase;
    private final JoinGroupByInviteUseCase joinGroupByInviteUseCase;
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

    @GetMapping("/groups")
    @Operation(
            summary = "Buscar grupos",
            description = "Informações sobre grupos do usuário",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<GroupResponse>> getGroups(HttpServletRequest request) {
        UUID userId = authenticationUtil.getUserIdFromRequest(request);
        List<GroupResponse> groups = getGroupUseCase.execute(userId);

        return ResponseEntity.ok(groups);
    }

    @PostMapping(path = "/groups", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Criar grupo",
            description = "Cria um novo grupo com thumbnail opcional. Envie 'name' e 'description' como campos de formulário, e 'thumbnail' como arquivo.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @ModelAttribute CreateGroupRequest request,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        GroupResponse response = createGroupUseCase.execute(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(path = "/groups/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Editar grupo",
            description = "Edita um grupo existente. Apenas os campos enviados serão atualizados.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<GroupResponse> editGroup(
            @PathVariable UUID groupId,
            @ModelAttribute EditGroupRequest request,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        GroupResponse response = editGroupUseCase.execute(groupId, request, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/groups/{groupId}/members")
    @Operation(
            summary = "Buscar membros de um grupo determinado",
            description = "Membros do grupo determinado",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<UserResponse>> getMembersByGroup(HttpServletRequest httpRequest, @PathVariable UUID groupId) {
        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        List<UserResponse> members = getGroupMembersUseCase.execute(groupId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    @DeleteMapping("/groups/{groupId}")
    @Operation(
            summary = "Sair ou deletar grupo",
            description = "Se o usuário for o dono, o grupo é deletado. Se não, o usuário sai do grupo.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<Void> leaveOrDeleteGroup(@PathVariable UUID groupId, HttpServletRequest request) {
        UUID userId = authenticationUtil.getUserIdFromRequest(request);
        leaveOrDeleteGroupUseCase.execute(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/groups/{groupId}/invite")
    @Operation(
            summary = "Gerar convite de grupo",
            description = "Gera um token de convite válido por 24 horas para o grupo.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<MessageResponse> generateInvite(@PathVariable UUID groupId, HttpServletRequest request) {
        UUID userId = authenticationUtil.getUserIdFromRequest(request);
        String token = generateGroupInviteUseCase.execute(groupId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(token));
    }

    @GetMapping("/groups/{groupId}/invite")
    @Operation(
            summary = "Buscar convite válido do grupo",
            description = "Retorna o token de convite válido mais recente do grupo, ou vazio se não houver.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<MessageResponse> getInvite(@PathVariable UUID groupId, HttpServletRequest request) {
        UUID userId = authenticationUtil.getUserIdFromRequest(request);
        MessageResponse response = getGroupInviteUseCase.execute(groupId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/groups/join/{token}")
    @Operation(
            summary = "Entrar no grupo via convite",
            description = "Adiciona o usuário ao grupo usando um token de convite válido.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<JoinGroupResponse> joinGroup(@PathVariable String token, HttpServletRequest request) {
        UUID userId = authenticationUtil.getUserIdFromRequest(request);
        UUID groupId = joinGroupByInviteUseCase.execute(token, userId);
        return ResponseEntity.ok(JoinGroupResponse.builder()
                .groupId(groupId)
                .message("Entrou no grupo com sucesso")
                .build());
    }
}