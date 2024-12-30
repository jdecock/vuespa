package com.jdecock.vuespa.services;

import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.RefreshTokenRepository;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.utils.SecurityCipher;
import com.jdecock.vuespa.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {
	private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
	private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	@Value("${jwt.secret-key}")
	private String jwtSecret;

	@Value("${jdecock.security.access-token-lifetime}")
	private int accessTokenLifetimeSeconds;

	@Value("${jdecock.security.refresh-token-lifetime}")
	private int refreshTokenLifetimeSeconds;

	@Value("${jdecock.security.cookie-secret-key}")
	private String cookieSecretKey;

	@Value("${jdecock.security.cookie-salt}")
	private String cookieSalt;

	public JwtService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
	}

	public String generateToken(HttpServletResponse response, String username) {
		String token = createToken(new HashMap<>(), username);
		setAccessTokenCookie(response, token);
		return token;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public void createRefreshToken(HttpServletResponse response, String email, boolean persistLogin) {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null)
			return;

		String token = UUID.randomUUID().toString();
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken(token);
		refreshToken.setPersistLogin(persistLogin);
		refreshToken.setExpiration(new Date(System.currentTimeMillis() + (accessTokenLifetimeSeconds * 1000L)));
		refreshTokenRepository.save(refreshToken);

		setRefreshTokenCookie(response, token, persistLogin);
	}

	public RefreshToken verifyRefreshToken(RefreshToken refreshToken) {
		if (refreshToken == null || refreshToken.getExpiration() == null || refreshToken.getExpiration().before(new Date())) {
			// If this is an expired token, delete it.
			if (refreshToken != null)
				refreshTokenRepository.delete(refreshToken);
			return null;
		}

		return refreshToken;
	}

	public String getAccessTokenValue(HttpServletRequest request) {
		Cookie cookie = getCookie(request, ACCESS_TOKEN_COOKIE_NAME);
		String tokenValue = cookie == null ? null : cookie.getValue();
		return StringUtils.isSet(tokenValue) ? SecurityCipher.decrypt(tokenValue, cookieSecretKey, cookieSalt) : null;
	}

	public String getRefreshTokenValue(HttpServletRequest request) {
		Cookie cookie = getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
		String tokenValue = cookie == null ? null : cookie.getValue();
		return StringUtils.isSet(tokenValue) ? SecurityCipher.decrypt(tokenValue, cookieSecretKey, cookieSalt) : null;
	}

	public void removeAuthenticationTokens(HttpServletRequest request, HttpServletResponse response) {
		// Delete the refresh token in the repository
		String refreshTokenValue = getRefreshTokenValue(request);
		refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(refreshTokenRepository::delete);

		deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME);
		deleteCookie(response, ACCESS_TOKEN_COOKIE_NAME);
	}

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	private String createToken(Map<String, Object> claims, String username) {
		return Jwts.builder()
			.claims(claims)
			.subject(username)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + (accessTokenLifetimeSeconds * 1000L)))
			.signWith(getSecretKey(), Jwts.SIG.HS256)
			.compact();
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parser()
			.verifyWith(getSecretKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
		return claimsResolver.apply(claims);
	}

	private Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		return cookies != null
			? Arrays.stream(cookies)
				.filter(x -> x.getName() != null && x.getName().equals(cookieName))
				.findFirst()
				.orElse(null)
			: null;
	}

	private void setAccessTokenCookie(HttpServletResponse response, String accessTokenValue) {
		String encryptedValue = SecurityCipher.encrypt(accessTokenValue, cookieSecretKey, cookieSalt);
		if (StringUtils.isSet(encryptedValue)) {
			Cookie httpCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, encryptedValue);
			httpCookie.setMaxAge(-1);
			httpCookie.setHttpOnly(true);
			httpCookie.setPath("/");
			response.addCookie(httpCookie);
		}
	}

	private void setRefreshTokenCookie(HttpServletResponse response, String refreshTokenValue, boolean persistLogin) {
		String encryptedValue = SecurityCipher.encrypt(refreshTokenValue, cookieSecretKey, cookieSalt);
		if (StringUtils.isSet(encryptedValue)) {
			Cookie httpCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, encryptedValue);
			httpCookie.setMaxAge(persistLogin ? refreshTokenLifetimeSeconds : -1);
			httpCookie.setHttpOnly(true);
			httpCookie.setPath("/");
			response.addCookie(httpCookie);
		}
	}

	private void deleteCookie(HttpServletResponse response, String cookieName) {
		Cookie httpCookie = new Cookie(cookieName, "");
		httpCookie.setMaxAge(0);
		httpCookie.setHttpOnly(true);
		httpCookie.setPath("/");
		response.addCookie(httpCookie);
	}
}
