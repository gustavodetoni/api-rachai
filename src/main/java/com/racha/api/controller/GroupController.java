package com.racha.api.controller;

import com.racha.api.dto.group.CreateGroupRequest;
import com.racha.api.dto.group.GroupResponse;
import com.racha.api.usecase.group.CreateGroupUseCase;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Grupos", description = "Gerenciamento de grupos")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final AuthenticationUtil authenticationUtil;

//    @GetMapping("/groups")
//    @Operation(
//            summary = "Buscar grupos",
//            description = "Informações sobre grupos do usuário",
//            security = @SecurityRequirement(name = "Bearer Authentication")
//    )
//    public ResponseEntity<User> getGroups(HttpServletRequest request) {
//        UUID userId = authenticationUtil.getUserIdFromRequest(request);
//
//        return ResponseEntity.ok();
//    }

    @PostMapping(path = "/groups", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Criar grupo",
            description = "Cria um novo grupo com thumbnail opcional. Envie 'name' e 'description' como campos de formulário, e 'thumbnail' como arquivo.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @ModelAttribute CreateGroupRequest request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            HttpServletRequest httpRequest) {

        UUID userId = authenticationUtil.getUserIdFromRequest(httpRequest);
        GroupResponse response = createGroupUseCase.execute(request, thumbnail, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}