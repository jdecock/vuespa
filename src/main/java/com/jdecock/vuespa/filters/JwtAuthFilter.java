package com.jdecock.vuespa.filters;

import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.RefreshTokenRepository;
import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import com.jdecock.vuespa.utils.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
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
	private final RefreshTokenRepository refreshTokenRepository;

	public JwtAuthFilter(final JwtService jwtService, final UserService userService, RefreshTokenRepository refreshTokenRepository) {
		this.jwtService = jwtService;
		this.userService = userService;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Override
	protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
	                                @NotNull FilterChain filterChain) throws ServletException, IOException {
		try {
			authenticateFromToken(request, response, null);
		} catch (ExpiredJwtException e) {
			// The auth token expired. Try to get a new one using the refresh token.
			refreshAccessToken(request, response);
		}

		// Continue the filter chain
		filterChain.doFilter(request, response);
	}

	private void authenticateFromToken(HttpServletRequest request, HttpServletResponse response, String accessToken) {
		if (StringUtils.isEmpty(accessToken))
			accessToken = jwtService.getAccessTokenValue(request);
		String username = StringUtils.isSet(accessToken) ? jwtService.extractUsername(accessToken) : null;

		// If the token is valid and no authentication is set in the context
		if (StringUtils.isSet(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
				null, userDetails.getAuthorities());
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		} else {
			refreshAccessToken(request, response);
		}
	}

	private void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
		// Check to see if there's a refresh token
		String tokenValue = jwtService.getRefreshTokenValue(request);
		if (StringUtils.isEmpty(tokenValue)) {
			jwtService.removeAuthenticationTokens(request, response);
			return;
		}

		RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue).orElse(null);
		refreshToken = jwtService.verifyRefreshToken(refreshToken);
		User user = refreshToken == null ? null : refreshToken.getUser();
		boolean persistLogin = refreshToken != null && refreshToken.isPersistLogin();

		if (refreshToken == null || user == null) {
			jwtService.removeAuthenticationTokens(request, response);
			return;
		}

		// The refresh token should only be used once.
		refreshTokenRepository.delete(refreshToken);

		// Issue new access and refresh token.
		String refreshedAccessToken = jwtService.generateToken(response, user.getEmail());
		jwtService.createRefreshToken(response, user.getEmail(), persistLogin);

		authenticateFromToken(request, response, refreshedAccessToken);
	}
}
