package com.lcwd.gateway.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.gateway.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String roleUser);
}