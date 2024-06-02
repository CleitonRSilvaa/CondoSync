package com.CondoSync.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.CondoSync.models.Morador;
import com.CondoSync.models.User;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {

	@Autowired
	private JwtEncoder jwtEncoder;

	@Value("${jwt.expiration.ofMinutes}")
	private int durationToExpire;

	@Autowired
	MoradorService moradorService;

	public String generateToken(User user, Duration duration) {
		Instant now = Instant.now();

		UUID id = moradorService.findByEmail(user.getUsername())
				.map(Morador::getId)
				.orElse(user.getId());

		String roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("CondSync-App")
				.issuedAt(now)
				.expiresAt(now.plus(Duration.ofMinutes(durationToExpire)))
				.subject(user.getUsername())
				.claim("id", id)
				.claim("name", user.getFullName())
				.claim("scope", roles)
				.claim("passwordExpiration", user.isCredentialsExpired())
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public String generateToken(User user) {
		Instant now = Instant.now();

		UUID id = moradorService.findByEmail(user.getUsername())
				.map(Morador::getId)
				.orElse(user.getId());

		String roles = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("CondSync-App")
				.issuedAt(now)
				.expiresAt(now.plus(duration))
				.subject(user.getUsername())
				.claim("id", id)
				.claim("name", user.getFullName())
				.claim("scope", roles)
				.claim("passwordExpiration", user.isCredentialsExpired())
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	class IdHolder {
		UUID id;
	}
}
