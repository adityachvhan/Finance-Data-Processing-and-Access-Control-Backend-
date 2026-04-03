package com.zorvyn.finance_dashboard.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zorvyn.finance_dashboard.entity.User;
import com.zorvyn.finance_dashboard.repository.UserRepository;

@Service
public class CustomUserDetailsServiceImplementation implements UserDetailsService {

	private UserRepository userRepository;

	public CustomUserDetailsServiceImplementation(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is inactive: " + email);
        }
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), authorities
        );
    }

}
