package com.jdecock.vuespa.services;

import com.jdecock.vuespa.entities.RefreshToken;
import com.jdecock.vuespa.entities.User;
import com.jdecock.vuespa.repositories.RefreshTokenRepository;
import com.jdecock.vuespa.repositories.UserRepository;
import com.jdecock.vuespa.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {
	public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

	// TODO: Reset the access token lifetime after sorting all the authentication stuff
	public static final int ACCESS_TOKEN_LIFETIME = 30000; // 30 * 60 * 1000; // Thirty minutes
	private static final int RECOVERY_TOKEN_LIFETIME = 7 * 24 * 60 * 60 * 1000; // Seven days

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	@Value("${jwt.secret-key}")
	private String jwtSecret;

	public JwtService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.userRepository = userRepository;
	}

	public String generateToken(String username) {
		return createToken(new HashMap<>(), username);
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		boolean isTokenExpired = extractExpiration(token).before(new Date());

		return StringUtils.isSet(username) && username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public RefreshToken createRefreshToken(String email) {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null)
			return null;

		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpiration(new Date(System.currentTimeMillis() + RECOVERY_TOKEN_LIFETIME));

		return refreshTokenRepository.save(refreshToken);
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

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	private String createToken(Map<String, Object> claims, String username) {
		return Jwts.builder()
			.claims(claims)
			.subject(username)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFETIME))
			.signWith(getSecretKey(), Jwts.SIG.HS256)
			.compact();
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		var claims = Jwts.parser()
			.verifyWith(getSecretKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
		return claimsResolver.apply(claims);
	}
}
