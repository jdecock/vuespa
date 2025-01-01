package com.jdecock.vuespa.config;

import com.jdecock.vuespa.filters.JwtAuthFilter;
import com.jdecock.vuespa.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtAuthFilter jwtAuthFilter;
	private final UserService userService;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserService userService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userService = userService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(x -> x
				.requestMatchers(
					"/api/auth/sign-up",
					"/api/auth/login",
					"/api/auth/logout"
				).permitAll()
				.requestMatchers(
					"/api/user",
					"/api/user/change-password",
					"/api/user/update"
				).hasAnyAuthority("ROLE_USER")
				.anyRequest().denyAll() // Deny all other endpoints
			)
			.sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
			.authenticationProvider(authenticationProvider()) // Custom authentication provider
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

		return http.build();
 	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userService);
		daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
