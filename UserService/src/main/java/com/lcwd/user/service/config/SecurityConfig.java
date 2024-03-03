package com.lcwd.user.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lcwd.user.service.security.JwtAuthenticationEntryPoint;
import com.lcwd.user.service.security.JwtRequestFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationEntryPoint point;

	@Autowired
	private JwtRequestFilter filter;

	@Bean
	public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/users/**", "/course/**").authenticated()
						.requestMatchers("/auth/login", "/auth/signup").permitAll().anyRequest().authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(point))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

		return http.build();

	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

}
