package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.security.*;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.controller.UserController;
import com.example.SkillSwap.entity.Permission;
import com.example.SkillSwap.entity.Role;
import com.example.SkillSwap.entity.RolePermission;
import com.example.SkillSwap.repository.PermissionRepository;
import com.example.SkillSwap.repository.RolePermissionRepository;
import com.example.SkillSwap.repository.RoleRepository;
import com.example.SkillSwap.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UsernameNotFoundException("Роль с идентификатором не найдена: " + roleId));
        return mapToRoleResponse(role);
    }


    @Override
    public RoleResponseDTO getRoleByName(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        return mapToRoleResponse(role);
    }

    @Override
    public List<RoleResponseDTO> getSubordinateRoles(Long parentRoleId) {
        RoleResponseDTO roleParentResponse = getRoleById(parentRoleId);
        Role roleParent = roleRepository.findByRoleName(roleParentResponse.roleName());
        List<Role> role = roleRepository.findByParentRole(roleParent);
        return role.stream().map(this::mapToRoleResponse).collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO addRole(RoleRequestDTO roleDTO) {
        logger.info("Добавление роли: {}", roleDTO);

        Role role;

        if (roleDTO.parentRoleId() != null) {
            logger.info("Ищем родительскую роль с идентификатором: {}", roleDTO.parentRoleId());
            Role parentRole = roleRepository.findById(roleDTO.parentRoleId())
                    .orElseThrow(() -> new CommonException("Родительская роль не найдена с идентификатором: " + roleDTO.parentRoleId()));
            role = new Role(roleDTO.roleName(), roleDTO.description(), parentRole);
        } else {
            role = new Role(roleDTO.roleName(), roleDTO.description());
        }

        Role savedRole = roleRepository.save(role);
        logger.info("Роль успешно сохранена: {}", savedRole);
        return mapToRoleResponse(role);
    }

    @Override
    public void assignPermissionToRole(RolePermissionAssignmentRequestDTO assignmentDTO) {
        Role role = roleRepository.findById(assignmentDTO.roleId())
                .orElseThrow(() -> new UsernameNotFoundException("Роль с идентификатором не найдена: " + assignmentDTO.roleId()));
        Permission permission = permissionRepository.findById(assignmentDTO.permissionId())
                .orElseThrow(() -> new CommonException("Разрешение не найдено с идентификатором: " + assignmentDTO.permissionId()));

        RolePermission rolePermission = new RolePermission(role, permission);
        rolePermissionRepository.save(rolePermission);
    }

    private RoleResponseDTO mapToRoleResponse(Role role) {
        return new RoleResponseDTO(
                role.getRoleId(),
                role.getRoleName(),
                role.getDescription());
    }
}

