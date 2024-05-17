package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.CondoSync.models.User;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

	@Autowired
	private JwtEncoder jwtEncoder;

	public String generateToken(User user, Duration duration) {
		Instant now = Instant.now();

		String roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("CondSync-App")
				.issuedAt(now)
				.expiresAt(now.plus(duration))
				.subject(user.getUsername())
				.claim("name", user.getFullName())
				.claim("scope", roles)
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public String generateToken(User user) {
		Instant now = Instant.now();
		String roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(now.plus(Duration.ofMinutes(30)))
				.subject(user.getUsername())
				.claim("name", user.getFullName())
				.claim("scope", roles)
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}
}
