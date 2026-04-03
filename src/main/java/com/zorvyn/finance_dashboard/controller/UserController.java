package com.zorvyn.finance_dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zorvyn.finance_dashboard.dto.UserDto;
import com.zorvyn.finance_dashboard.execption.ConflictException;
import com.zorvyn.finance_dashboard.execption.ResourceNotFoundException;
import com.zorvyn.finance_dashboard.repository.UserRepository;
import com.zorvyn.finance_dashboard.request.UpdateRequest;
import com.zorvyn.finance_dashboard.request.UpdateRoleRequest;
import com.zorvyn.finance_dashboard.response.ApiResponse;
import com.zorvyn.finance_dashboard.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private UserService userService;

	private UserRepository userRepository;

	public UserController(UserService userService, UserRepository userRepository) {
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserDto>> getAllUsers() {

		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<UserDto> getUserById(@PathVariable Long id) throws ResourceNotFoundException {

		return ResponseEntity.ok(userService.getUserById(id));
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id,
			@Valid @RequestBody UpdateRoleRequest roleRequest) throws ResourceNotFoundException {

		return ResponseEntity.ok(userService.updateRole(roleRequest, id));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
	public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateRequest request)
			throws ResourceNotFoundException, ConflictException {
		return ResponseEntity.ok(userService.updateUser(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) throws ResourceNotFoundException {
		userService.deleteUser(id);
		return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
	}

}
