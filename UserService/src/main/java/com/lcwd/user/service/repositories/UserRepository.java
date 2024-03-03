package com.lcwd.user.service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.user.service.entities.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String username);
}
