package com.jdecock.vuespa.services;

import com.jdecock.vuespa.entities.UserInfo;
import com.jdecock.vuespa.repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService implements UserDetailsService {
	@Autowired
	private UserInfoRepository userInfoRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var userDetail = userInfoRepository.findByEmail(username);
		return userDetail.map(UserInfoDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	public String addUser(UserInfo userInfo) {
		var encoder = new BCryptPasswordEncoder();
		userInfo.setPassword(encoder.encode(userInfo.getPassword()));
		userInfoRepository.save(userInfo);
		return "User Added Successfully";
	}
}
