package com.jdecock.vuespa.filters;

import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserInfoService userInfoService;

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
		throws ServletException, IOException {
		// Retrieve the Authorization header
		var authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		// Check if the header starts with "Bearer "
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtService.extractUsername(token);
		}

		// If the token is valid and no authentication is set in the context
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			var userDetails = userInfoService.loadUserByUsername(username);

			// Validate token and set authentication
			if (jwtService.validateToken(token, userDetails)) {
				var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		// Continue the filter chain
		filterChain.doFilter(request, response);
	}
}
