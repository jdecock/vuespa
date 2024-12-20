package com.jdecock.vuespa.services;

import com.jdecock.vuespa.entities.UserInfo;
import com.jdecock.vuespa.repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserInfo> userDetail = repository.findByEmail(username); // Assuming 'email' is used as username

		// Converting UserInfo to UserDetails
		return userDetail.map(UserInfoDetails::new)
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	public String addUser(UserInfo userInfo) {
		userInfo.setPassword(encoder.encode(userInfo.getPassword()));
		repository.save(userInfo);
		return "User Added Successfully";
	}
}
