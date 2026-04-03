package com.zorvyn.finance_dashboard.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateRequest {

	@Email(message = "Invalid email format")
	private String email;
}
