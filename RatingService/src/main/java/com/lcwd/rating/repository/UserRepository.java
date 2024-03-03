package com.lcwd.rating.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.rating.entities.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String username);
}
