package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findById(Long id);
    Role findByRoleName(String roleName);
    List<Role> findByParentRole(Role parentRole);
}
