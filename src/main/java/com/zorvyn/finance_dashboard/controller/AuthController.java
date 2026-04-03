package com.zorvyn.finance_dashboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zorvyn.finance_dashboard.execption.ConflictException;
import com.zorvyn.finance_dashboard.request.LoginRequest;
import com.zorvyn.finance_dashboard.request.RegisterRequest;
import com.zorvyn.finance_dashboard.response.AuthResponse;
import com.zorvyn.finance_dashboard.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private AuthService authService;

	public AuthController(AuthService authService) {

		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) throws ConflictException {

		AuthResponse authResponse = authService.register(request);

		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) throws ConflictException {

		AuthResponse authResponse = authService.login(request);

		return new ResponseEntity<>(authResponse,HttpStatus.OK);

	}
}
