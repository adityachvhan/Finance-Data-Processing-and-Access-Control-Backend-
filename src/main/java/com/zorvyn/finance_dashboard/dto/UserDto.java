package com.zorvyn.finance_dashboard.dto;

import java.time.LocalDateTime;

import com.zorvyn.finance_dashboard.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;
	private String username;
	private String email;
	private Role role;
	private boolean active;
	private LocalDateTime createdAt;
}
