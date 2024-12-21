package com.jdecock.vuespa.services;

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
import java.util.function.Function;

@Component
public class JwtService {
	private static final int THIRTY_MINUTES_IN_MILLIS = 1000 * 60 * 30;

	@Value("${jwt.secret-key}")
	private String jwtSecret;

	public String generateToken(String userName) {
		return createToken(new HashMap<>(), userName);
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		var username = extractUsername(token);
		var isTokenExpired = extractExpiration(token).before(new Date());

		return username.equals(userDetails.getUsername()) && !isTokenExpired;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder()
			.claims(claims)
			.subject(userName)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + THIRTY_MINUTES_IN_MILLIS))
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
