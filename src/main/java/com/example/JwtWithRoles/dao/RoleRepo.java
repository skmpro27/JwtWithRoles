package com.example.JwtWithRoles.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.JwtWithRoles.modal.Role;

public interface RoleRepo extends JpaRepository<Role, Integer> {
	Role findRoleByName(String name);
}
