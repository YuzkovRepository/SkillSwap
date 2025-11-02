package com.example.SkillSwap.controller;

import com.example.SkillSwap.dto.SuccessResponseDTO;
import com.example.SkillSwap.dto.SuccessResponseWithModelDTO;
import com.example.SkillSwap.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.SkillSwap.dto.security.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Пользователи", description = "Методы регистрации, аутентификации и управления ролями/правами пользователей")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя и возвращает данные зарегистрированного пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован")
    @PostMapping("/register")
    public ResponseEntity<SuccessResponseWithModelDTO> registerUser(@RequestBody RegisterRequestDTO requestDTO) {
        RegisterResponseDTO registeredUser = userService.registerUser(requestDTO);
        SuccessResponseWithModelDTO response = new SuccessResponseWithModelDTO(
                HttpStatus.CREATED.value(),
                "The user has successfully registered",
                registeredUser
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Аутентификация пользователя", description = "Возвращает JWT-токен при успешной аутентификации")
    @ApiResponse(responseCode = "202", description = "Пользователь успешно аутентифицирован")
    @PostMapping("/login")
    public ResponseEntity<SuccessResponseWithModelDTO> authenticate(@Validated @RequestBody LoginRequestDTO requestDTO) {
        LoginResponseDTO authResponse = userService.authenticate(requestDTO);
        SuccessResponseWithModelDTO response = new SuccessResponseWithModelDTO(
                HttpStatus.ACCEPTED.value(),
                "The user has successfully logged in",
                authResponse
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(summary = "Получить роли пользователя", description = "Возвращает список ролей, назначенных пользователю. Требуются права VIEW_ROLES")
    @ApiResponse(responseCode = "200", description = "Роли пользователя успешно получены")
    @PreAuthorize("hasAuthority('VIEW_ROLES')")
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleResponseDTO>> getUserRoles(@PathVariable Long userId) {
        try {
            List<RoleResponseDTO> roles = userService.getUserRoles(userId);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Error when getting user roles with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Получить права пользователя", description = "Возвращает список прав пользователя. Требуются права VIEW_PERMISSIONS")
    @ApiResponse(responseCode = "200", description = "Права пользователя успешно получены")
    @PreAuthorize("hasAuthority('VIEW_PERMISSIONS')")
    @GetMapping("/{userId}/permissions")
    public ResponseEntity<List<PermissionResponseDTO>> getUserPermissions(@PathVariable Long userId) {
        List<PermissionResponseDTO> permissions = userService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Назначить роль пользователю", description = "Назначает указанную роль пользователю. Требуются права ASSIGN_ROLE")
    @ApiResponse(responseCode = "200", description = "Роль успешно назначена пользователю")
    @PreAuthorize("hasAuthority('ASSIGN_ROLE')")
    @PostMapping("/assign-role")
    public ResponseEntity<SuccessResponseDTO> assignRoleToUser(@RequestBody UserRoleAssignmentRequestDTO assignmentDTO) {
        userService.assignRoleToUser(assignmentDTO);
        SuccessResponseDTO response = new SuccessResponseDTO(
                HttpStatus.ACCEPTED.value(),
                "The role was successfully assigned to the user "+assignmentDTO.userId()
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
