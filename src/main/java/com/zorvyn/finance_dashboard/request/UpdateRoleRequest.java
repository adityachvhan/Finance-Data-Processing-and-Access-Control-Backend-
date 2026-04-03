package com.zorvyn.finance_dashboard.request;

import com.zorvyn.finance_dashboard.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {

	@NotNull(message = "Role is required")
	private Role role;
}
