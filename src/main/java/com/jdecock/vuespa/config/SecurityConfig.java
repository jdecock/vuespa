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

	public SecurityConfig(final JwtAuthFilter jwtAuthFilter, final UserService userService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userService = userService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs
			.authorizeHttpRequests(x -> x
				.requestMatchers(
					"/auth/sign-up",
					"/auth/generate-token"
//					"/auth/welcome",
				).permitAll()
				.requestMatchers(
					"/user/"
				).hasAnyAuthority("ROLE_USER")
//				.requestMatchers(
//					"/auth/user/**"
//				).hasAuthority("ROLE_USER")
//				.requestMatchers(
//					"/auth/admin/**"
//				).hasAuthority("ROLE_ADMIN")
//				.anyRequest().authenticated() // Protect all other endpoints
				.anyRequest().denyAll() // Deny all other endpoints
			)
			.sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
			.authenticationProvider(authenticationProvider()) // Custom authentication provider
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		var authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userService);
		authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
