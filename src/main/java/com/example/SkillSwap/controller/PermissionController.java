package com.example.SkillSwap.controller;

import com.example.SkillSwap.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.SkillSwap.dto.security.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/permissions")
@Tag(name = "Права доступа", description = "Методы управления правами доступа (permissions)")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Получить все права", description = "Возвращает список всех прав. Требуются права VIEW_PERMISSIONS")
    @ApiResponse(responseCode = "200", description = "Список прав успешно получен")
    @PreAuthorize("hasAuthority('VIEW_PERMISSIONS')")
    @GetMapping
    public ResponseEntity<List<PermissionResponseDTO>> getAllPermissions() {
        List<PermissionResponseDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Создать новое право", description = "Создаёт новое право. Требуются права ADD_PERMISSIONS")
    @ApiResponse(responseCode = "200", description = "Право успешно создано")
    @PreAuthorize("hasAuthority('ADD_PERMISSIONS')")
    @PostMapping("/create")
    public ResponseEntity<PermissionResponseDTO> addPermission(@RequestBody PermissionRequestDTO permissionDTO) {
        PermissionResponseDTO permission = permissionService.addPermission(permissionDTO);
        return ResponseEntity.ok(permission);
    }

    @Operation(summary = "Получить право по ID", description = "Возвращает право по его идентификатору. Требуются права VIEW_PERMISSIONS")
    @ApiResponse(responseCode = "200", description = "Право успешно найдено")
    @PreAuthorize("hasAuthority('VIEW_PERMISSIONS')")
    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionResponseDTO> getPermissionById(@PathVariable Long permissionId) {
        PermissionResponseDTO permission = permissionService.getPermissionById(permissionId);
        return ResponseEntity.ok(permission);
    }

    @Operation(summary = "Получить право по названию", description = "Возвращает право по его названию. Требуются права VIEW_PERMISSIONS")
    @ApiResponse(responseCode = "200", description = "Право успешно найдено")
    @PreAuthorize("hasAuthority('VIEW_PERMISSIONS')")
    @GetMapping("/name/{permissionName}")
    public ResponseEntity<PermissionResponseDTO> getPermissionByName(@PathVariable String permissionName) {
        PermissionResponseDTO permission = permissionService.getPermissionByName(permissionName);
        return ResponseEntity.ok(permission);
    }
}
