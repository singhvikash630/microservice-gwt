package com.lcwd.gateway.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.gateway.dto.JwtRequest;
import com.lcwd.gateway.dto.JwtResponse;
import com.lcwd.gateway.dto.MessageResponse;
import com.lcwd.gateway.dto.SignupRequest;
import com.lcwd.gateway.entities.Role;
import com.lcwd.gateway.entities.User;
import com.lcwd.gateway.repositories.RoleRepository;
import com.lcwd.gateway.repositories.UserRepository;
import com.lcwd.gateway.security.JwtTokenUtil;
import com.lcwd.gateway.security.JwtUserDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@PostMapping("/login")
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody JwtRequest request) {

		this.doAuthenticate(request.getEmail(), request.getPassword());

		UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getEmail());
		String token = jwtTokenUtil.generateToken(userDetails);

		JwtResponse response = JwtResponse.builder().jwttoken(token).username(userDetails.getUsername()).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByName(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName("user")
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName("admin")
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName("moderator")
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName("user")
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	private void doAuthenticate(String email, String password) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
		try {
			authenticationManager.authenticate(authentication);

		} catch (BadCredentialsException e) {
			throw new BadCredentialsException(" Invalid Username or Password  !!");
		}

	}

	@ExceptionHandler(BadCredentialsException.class)
	public String exceptionHandler() {
		return "Credentials Invalid !!";
	}

}
