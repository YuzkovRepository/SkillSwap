package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.security.*;
import com.example.SkillSwap.entity.Permission;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.PermissionRepository;
import com.example.SkillSwap.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findAll().stream().map(this::mapToPermissionResponse).collect(Collectors.toList());
    }

    @Override
    public PermissionResponseDTO getPermissionById(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new CommonException("Права роли не найдены"));
        return mapToPermissionResponse(permission);
    }

    @Override
    public PermissionResponseDTO getPermissionByName(String permissionName) {
        Permission permission = permissionRepository.findByPermissionName(permissionName).orElseThrow(() -> new CommonException("Права роли не найдены"));
        return mapToPermissionResponse(permission);
    }

    @Override
    public PermissionResponseDTO addPermission(PermissionRequestDTO permissionDTO) {
        Permission permission = new Permission(permissionDTO.permissionName(), permissionDTO.description());
        permissionRepository.save(permission);
        return mapToPermissionResponse(permission);
    }

    private PermissionResponseDTO mapToPermissionResponse(Permission permission) {
        return new PermissionResponseDTO(
                permission.getPermissionsId(),
                permission.getPermissionName(),
                permission.getDescription());
    }
}