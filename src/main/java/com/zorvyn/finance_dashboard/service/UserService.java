package com.zorvyn.finance_dashboard.service;

import java.util.List;

import com.zorvyn.finance_dashboard.dto.UserDto;
import com.zorvyn.finance_dashboard.entity.User;
import com.zorvyn.finance_dashboard.execption.ConflictException;
import com.zorvyn.finance_dashboard.execption.ResourceNotFoundException;
import com.zorvyn.finance_dashboard.request.UpdateRequest;
import com.zorvyn.finance_dashboard.request.UpdateRoleRequest;
import com.zorvyn.finance_dashboard.request.UpdateStatusRequest;

public interface UserService {

	public User findById(Long id) throws ResourceNotFoundException;

	List<UserDto> getAllUsers();

	public UserDto getUserById(Long id) throws ResourceNotFoundException;

	public UserDto updateRole(UpdateRoleRequest roleRequest, Long id) throws ResourceNotFoundException;

	public UserDto updateStatus(Long id, UpdateStatusRequest statusRequest) throws ResourceNotFoundException;

	public UserDto updateUser(long id, UpdateRequest updateRequest) throws ResourceNotFoundException, ConflictException;

	public UserDto toResponse(User user);

	public void deleteUser(Long id) throws ResourceNotFoundException;
}
