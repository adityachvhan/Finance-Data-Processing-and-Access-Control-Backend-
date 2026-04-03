package com.zorvyn.finance_dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zorvyn.finance_dashboard.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
