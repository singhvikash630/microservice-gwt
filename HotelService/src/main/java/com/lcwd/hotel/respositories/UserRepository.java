package com.lcwd.hotel.respositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lcwd.hotel.entites.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String username);
}
