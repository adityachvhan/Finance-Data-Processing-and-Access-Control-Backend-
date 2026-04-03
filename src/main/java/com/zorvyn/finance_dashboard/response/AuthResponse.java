package com.zorvyn.finance_dashboard.response;

import com.zorvyn.finance_dashboard.enums.Role;

import lombok.Data;

@Data
@lombok.AllArgsConstructor
public class AuthResponse {

	private String token;
	private String tokenType;
	private String username;
	private String email;
	private Role role;

	public AuthResponse(String token, String username, String email, Role role) {
		this.token = token;
		this.tokenType = "Bearer";
		this.username = username;
		this.email = email;
		this.role = role;
	}
}
