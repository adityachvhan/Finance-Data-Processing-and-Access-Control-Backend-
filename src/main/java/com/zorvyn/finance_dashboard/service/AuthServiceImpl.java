package com.zorvyn.finance_dashboard.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zorvyn.finance_dashboard.configuration.JwtTokenProvider;
import com.zorvyn.finance_dashboard.entity.User;
import com.zorvyn.finance_dashboard.execption.ConflictException;
import com.zorvyn.finance_dashboard.repository.UserRepository;
import com.zorvyn.finance_dashboard.request.LoginRequest;
import com.zorvyn.finance_dashboard.request.RegisterRequest;
import com.zorvyn.finance_dashboard.response.AuthResponse;

@Service
public class AuthServiceImpl implements AuthService {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private JwtTokenProvider jwtTokenProvider;
	private AuthenticationManager authenticationManager;

	public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.authenticationManager = authenticationManager;
	}

	@Override
	public AuthResponse register(RegisterRequest request) throws ConflictException {

		if (userRepository.existsByUsername(request.getUsername())) {

			throw new ConflictException("Username already taken: " + request.getUsername());
		}

		if (userRepository.existsByEmail(request.getEmail())) {

			throw new ConflictException("Email already registered: " + request.getEmail());
		}

		String email = request.getEmail();
		String password = request.getPassword();

		User user = User.builder().username(request.getUsername()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).active(true).build();

		userRepository.save(user);

		// Authenticate the user
		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Generate JWT token
		String token = jwtTokenProvider.generateToken(authentication);

		return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
	}

	@Override
	public AuthResponse login(LoginRequest request) {

		 Authentication authentication = authenticationManager
		            .authenticate(new UsernamePasswordAuthenticationToken(
		                    request.getEmail(),      // ← was request.getUsername()
		                    request.getPassword()
		            ));

		    SecurityContextHolder.getContext().setAuthentication(authentication);
		    String token = jwtTokenProvider.generateToken(authentication);

		    User user = userRepository.findByEmail(request.getEmail()); // ← was request.getUsername()

		    return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
	}

}
