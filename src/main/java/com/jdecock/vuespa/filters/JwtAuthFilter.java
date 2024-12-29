package com.jdecock.vuespa.filters;

import com.jdecock.vuespa.services.JwtService;
import com.jdecock.vuespa.services.UserService;
import com.jdecock.vuespa.utils.SecurityCipher;
import com.jdecock.vuespa.utils.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
		String accessToken = getAccessToken(request);
		String username = StringUtils.isSet(accessToken) ? jwtService.extractUsername(accessToken) : null;

		// If the token is valid and no authentication is set in the context
		if (StringUtils.isSet(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userService.loadUserByUsername(username);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
				null, userDetails.getAuthorities());
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authToken);
		} else {
			deleteAccessToken(request, response);
		}

		// Continue the filter chain
		filterChain.doFilter(request, response);
	}

	private Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return null;

		for (Cookie cookie : cookies) {
			if (StringUtils.isSet(cookie.getName()) && cookie.getName().equals(cookieName))
				return cookie;
		}

		return null;
	}

	private String getAccessToken(HttpServletRequest request) {
		Cookie accessTokenCookie = getCookie(request, JwtService.ACCESS_TOKEN_COOKIE_NAME);
		String accessToken = accessTokenCookie != null ? accessTokenCookie.getValue() : null;
		if (StringUtils.isEmpty(accessToken))
			return null;

		String decryptedToken = SecurityCipher.decrypt(accessToken);
		return StringUtils.isSet(decryptedToken) && decryptedToken.startsWith("Bearer ")
			? decryptedToken.substring(7)
			: decryptedToken;
	}

	protected void deleteAccessToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie accessTokenCookie = getCookie(request, JwtService.ACCESS_TOKEN_COOKIE_NAME);
		if (accessTokenCookie == null)
			return;

		accessTokenCookie.setMaxAge(0);
		response.addCookie(accessTokenCookie);
	}
}
