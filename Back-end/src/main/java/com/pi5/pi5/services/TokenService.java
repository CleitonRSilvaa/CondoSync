package com.pi5.pi5.services;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.pi5.pi5.models.User;

@Service
public class TokenService {

	@Autowired
	private JwtEncoder encoder;

	public String generateToken(User user, Duration duration) {
		Instant now = Instant.now();

		String scope = user.getRoles().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("self")
				// .audience(Arrays.asList(clientKey))
				.issuedAt(now)
				.expiresAt(now.plus(duration))
				.subject(user.getUsername())
				.claim("name", user.getFullName())
				.claim("scope", scope).build();

		JwtEncoderParameters encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),
				claims);
		return encoder.encode(encoderParameters).getTokenValue();
	}
}
