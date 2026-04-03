package com.zorvyn.finance_dashboard.service;

import com.zorvyn.finance_dashboard.execption.ConflictException;
import com.zorvyn.finance_dashboard.request.LoginRequest;
import com.zorvyn.finance_dashboard.request.RegisterRequest;
import com.zorvyn.finance_dashboard.response.AuthResponse;

public interface AuthService {

	public AuthResponse register(RegisterRequest request) throws ConflictException;

	public AuthResponse login(LoginRequest request) throws ConflictException;
}
