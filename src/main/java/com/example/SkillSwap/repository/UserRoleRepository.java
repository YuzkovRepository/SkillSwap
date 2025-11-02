package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.Permission;
import com.example.SkillSwap.entity.Role;
import com.example.SkillSwap.entity.User;
import com.example.SkillSwap.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("SELECT ur.role FROM UserRole ur WHERE ur.user.userId = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);
    @Query("SELECT p FROM Permission p " +
            "JOIN RolePermission rp ON p.permissionsId = rp.permission.permissionsId " +
            "JOIN Role r ON rp.role.roleId = r.roleId " +
            "JOIN UserRole ur ON r.roleId = ur.role.roleId " +
            "WHERE ur.user.userId = :userId")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    @Query("SELECT r.roleName FROM UserRole ur JOIN ur.role r WHERE ur.user.userId = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);
}
