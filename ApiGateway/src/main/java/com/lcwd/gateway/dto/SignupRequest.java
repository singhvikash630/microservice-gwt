package com.lcwd.gateway.dto;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

	@NotEmpty
	private String username;

	@NotEmpty(message = "Email should not be empty")
	@Email
	private String email;

	@NotEmpty(message = "Password should not be empty")
	private String password;

	private Set<String> roles = new HashSet<>();
}
