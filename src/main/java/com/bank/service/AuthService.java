package com.bank.service;

import com.bank.dto.*;
import com.bank.entity.Role;
import com.bank.entity.User;
import com.bank.repository.UserRepository;
import com.bank.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;

	public String register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new IllegalArgumentException("Username already exists");
		}
		User user = User.builder().username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword())).role(Role.USER).build();
		userRepository.save(user);
		return "User registered successfully";
	}

	public AuthResponse login(LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		UserDetails details = userDetailsService.loadUserByUsername(request.getUsername());
		User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
		return new AuthResponse(jwtService.generateToken(details), user.getUsername(), user.getRole().name());
	}
}
