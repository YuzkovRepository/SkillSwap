package com.example.SkillSwap.service;

import com.example.SkillSwap.dto.security.*;

import java.util.List;

public interface UserService {
    RegisterResponseDTO registerUser(RegisterRequestDTO requestDTO);
    LoginResponseDTO authenticate(LoginRequestDTO requestDTO);
    List<RoleResponseDTO> getUserRoles(Long userId);
    List<PermissionResponseDTO> getUserPermissions(Long userId);
    void assignRoleToUser(UserRoleAssignmentRequestDTO assignmentDTO);
    List<RolePermissionResponseDTO> getRolesAndPermissionsByLogin(String login);
}
