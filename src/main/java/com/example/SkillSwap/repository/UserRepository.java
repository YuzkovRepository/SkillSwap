package com.example.SkillSwap.repository;

import com.example.SkillSwap.dto.security.RolePermissionResponseDTO;
import com.example.SkillSwap.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);

    Optional<User> findByEmail(String email);

    Optional<User> findByLogin(String login);

    @Query("SELECT new com.example.SkillSwap.dto.security.RolePermissionResponseDTO(r.roleName, p.permissionName) " +
            "FROM User u JOIN u.userRoles ur JOIN ur.role r JOIN r.rolePermissions rp JOIN rp.permission p " +
            "WHERE u.login = :login")
    List<RolePermissionResponseDTO> findRolesAndPermissionsByLogin(@Param("login") String login);

    @Query("SELECT DISTINCT p.permissionName " +
            "FROM User u " +
            "JOIN u.userRoles ur " +
            "JOIN ur.role r " +
            "JOIN r.rolePermissions rp " +
            "JOIN rp.permission p " +
            "WHERE u.userId = :userId")
    List<String> findUserPermissions(@Param("userId") Long userId);
}
