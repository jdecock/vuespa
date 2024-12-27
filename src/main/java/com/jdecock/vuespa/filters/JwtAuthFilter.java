package com.jdecock.vuespa.filters;

import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import com.jdecock.vuespa.utils.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserService userService;

	public JwtAuthFilter(final JwtService jwtService, final UserService userService) {
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
	                                @NotNull FilterChain filterChain) throws ServletException, IOException {
		// Retrieve the Authorization header
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		// Check if the header starts with "Bearer "
		if (StringUtils.isSet(authHeader) && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtService.extractUsername(token);
		}

		// If the token is valid and no authentication is set in the context
		if (StringUtils.isSet(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userService.loadUserByUsername(username);

			// Validate token and set authentication
			if (jwtService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
					null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		// Continue the filter chain
		filterChain.doFilter(request, response);
	}
}
