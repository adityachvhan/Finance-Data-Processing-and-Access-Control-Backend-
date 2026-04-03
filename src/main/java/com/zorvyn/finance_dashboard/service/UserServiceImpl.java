package com.zorvyn.finance_dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zorvyn.finance_dashboard.dto.UserDto;
import com.zorvyn.finance_dashboard.entity.User;
import com.zorvyn.finance_dashboard.execption.ConflictException;
import com.zorvyn.finance_dashboard.execption.ResourceNotFoundException;
import com.zorvyn.finance_dashboard.repository.UserRepository;
import com.zorvyn.finance_dashboard.request.UpdateRequest;
import com.zorvyn.finance_dashboard.request.UpdateRoleRequest;
import com.zorvyn.finance_dashboard.request.UpdateStatusRequest;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User findById(Long id) throws ResourceNotFoundException {

		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
	}

	@Override
	public List<UserDto> getAllUsers() {

		List<User> users = userRepository.findAll();
		List<UserDto> responseList = new ArrayList<>();

		for (User user : users) {
			responseList.add(toResponse(user));
		}

		return responseList;
	}

	@Override
	public UserDto getUserById(Long id) throws ResourceNotFoundException {

		return toResponse(findById(id));
	}

	@Override
	@Transactional
	public UserDto updateRole(UpdateRoleRequest roleRequest, Long id) throws ResourceNotFoundException {

		User user = findById(id);
		user.setRole(roleRequest.getRole());
		return toResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public UserDto updateStatus(Long id, UpdateStatusRequest statusRequest) throws ResourceNotFoundException {

		User user = findById(id);
		user.setActive(statusRequest.getActive());
		return toResponse(userRepository.save(user));
	}

	@Override
	@Transactional
	public UserDto updateUser(long id, UpdateRequest updateRequest)
			throws ResourceNotFoundException, ConflictException {
		User user = findById(id);

		if (!updateRequest.getEmail().equals(user.getEmail())
				&& userRepository.existsByEmail(updateRequest.getEmail())) {

			throw new ConflictException("Email already in use:" + updateRequest.getEmail());
		}

		user.setEmail(updateRequest.getEmail());
		return toResponse(userRepository.save(user));
	}

	@Override
	public UserDto toResponse(User user) {

		return new UserDto(

				user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isActive(),
				user.getCreatedAt());
	}

	@Override
	public void deleteUser(Long id) throws ResourceNotFoundException {
		
		userRepository.delete(findById(id));
		
	}

}
