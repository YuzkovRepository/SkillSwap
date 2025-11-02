package com.example.SkillSwap.service;

import java.util.List;
import com.example.SkillSwap.dto.security.*;

public interface RoleService {
    List<RoleResponseDTO> getAllRoles();
    RoleResponseDTO getRoleById(Long roleId);
    RoleResponseDTO getRoleByName(String roleName);
    List<RoleResponseDTO> getSubordinateRoles(Long parentRoleId);
    RoleResponseDTO addRole(RoleRequestDTO roleDTO);
    void assignPermissionToRole(RolePermissionAssignmentRequestDTO assignmentDTO);
}
