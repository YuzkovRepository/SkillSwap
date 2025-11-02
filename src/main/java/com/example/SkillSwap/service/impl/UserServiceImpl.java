package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.security.*;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.entity.*;
import com.example.SkillSwap.repository.RoleRepository;
import com.example.SkillSwap.repository.UserRepository;
import com.example.SkillSwap.repository.UserRoleRepository;
import com.example.SkillSwap.security.JwtUtil;
import com.example.SkillSwap.security.Validators.ValidatorUser;
import com.example.SkillSwap.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ValidatorUser validatorUser;

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO request) {
        validatorUser.validateRegisterRequest(request);

        if (userRepository.existsByLogin(request.login())) {
            throw new CommonException("Логин уже существует");
        }

        User user = new User();
        user.setLogin(request.login());
        user.setPassword_hash(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setSurname(request.surname());

        User savedUser = userRepository.save(user);

        Role patientRole = roleRepository.findById(1L)
                .orElseThrow(() -> new CommonException("Роль пользователя не найдена"));
        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(patientRole);
        userRoleRepository.save(userRole);

        logger.info("Пользователь успешно зарегистрировался с ролью пациента: {}", savedUser.getLogin());

        return mapToRegisterResponse(savedUser);
    }

    private RegisterResponseDTO mapToRegisterResponse(User user) {
        return new RegisterResponseDTO(
                user.getLogin(),
                user.getEmail(),
                user.getPhone()
        );
    }

    @Transactional
    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        logger.info("An authentication request was received for: {}", loginRequestDTO.loginOrEmail());
        validatorUser.validateLoginRequest(loginRequestDTO);

        String loginOrEmail = loginRequestDTO.loginOrEmail();
        String password = loginRequestDTO.password();

        User user;
        if (loginOrEmail.contains("@")) {
            user = userRepository.findByEmail(loginOrEmail)
                    .orElseThrow(() -> new CommonException("The user was not found"));
        } else {
            user = userRepository.findByLogin(loginOrEmail)
                    .orElseThrow(() -> new CommonException("The user was not found"));
        }

        if (!passwordEncoder.matches(password, user.getPassword_hash())) {
            logger.error("Invalid password for the user: {}", loginOrEmail);
            throw new CommonException("Invalid password");
        }

        // ✅ Получаем только названия ролей без загрузки всей graph
        List<String> roles = userRoleRepository.findRoleNamesByUserId(user.getUserId());

        String token = jwtUtil.generateToken(user.getLogin(), roles);
        logger.info("Сгенерирован JWT-токен для пользователя: {}", user.getLogin());

        // ✅ Возвращаем DTO вместо сущности User
        return new LoginResponseDTO(
                token,
                user.getLogin(),
                user.getEmail()
        );
    }

    @Override
    public List<RoleResponseDTO> getUserRoles(Long userId) {
        try {
            List<Role> roles = userRoleRepository.findRolesByUserId(userId);

            return roles.stream()
                    .map(role -> new RoleResponseDTO(role.getRoleId(), role.getRoleName(),role.getRoleName()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Ошибка при выборе ролей для пользователя с идентификатором: {}", userId, e);
            throw e;
        }
    }

    @Override
    public List<PermissionResponseDTO> getUserPermissions(Long userId) {
        try {
            List<Permission> permissions = userRoleRepository.findPermissionsByUserId(userId);

            List<PermissionResponseDTO> permissionDTOs = permissions.stream()
                    .map(permission -> new PermissionResponseDTO(
                            permission.getPermissionsId(),
                            permission.getPermissionName(),
                            permission.getDescription()
                    ))
                    .collect(Collectors.toList());

            return permissionDTOs;
        } catch (Exception e) {
            logger.error("Ошибка при получении разрешений для пользователя с идентификатором: {}", userId, e);
            throw e;
        }
    }

    @Override
    public void assignRoleToUser(UserRoleAssignmentRequestDTO assignmentDTO) {
        User user = userRepository.findById(assignmentDTO.userId())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        Role role = roleRepository.findById(assignmentDTO.roleId())
                .orElseThrow(() -> new CommonException("Роль не найдена"));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepository.save(userRole);
    }

    @Override
    public List<RolePermissionResponseDTO> getRolesAndPermissionsByLogin(String login) {
        try {
            return userRepository.findRolesAndPermissionsByLogin(login);
        } catch (Exception e) {
            logger.error("Ошибка при выборе ролей и разрешений для входа в систему: {}", login, e);
            throw new CommonException("Не удалось получить роли и разрешения");
        }
    }
}
