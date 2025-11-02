package com.example.SkillSwap.service;

import com.example.SkillSwap.dto.security.*;

import java.util.List;

public interface PermissionService {
    List<PermissionResponseDTO> getAllPermissions();
    PermissionResponseDTO getPermissionById(Long permissionId);
    PermissionResponseDTO getPermissionByName(String permissionName);
    PermissionResponseDTO addPermission(PermissionRequestDTO permissionDTO);
}
