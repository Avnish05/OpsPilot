package com.opspilot.auth.infrastructure.security;

import com.opspilot.auth.application.AuthenticatedUser;
import com.opspilot.auth.application.AuthenticationToken;
import com.opspilot.auth.application.JwtTokenIssuer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Component;

@Component
public class RsaJwtTokenIssuer implements JwtTokenIssuer {
    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(15);
    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    public RsaJwtTokenIssuer(JwtEncoder jwtEncoder, Clock clock) { this.jwtEncoder = jwtEncoder; this.clock = clock; }
    @Override
    public AuthenticationToken issue(AuthenticatedUser user) {
        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plus(TOKEN_LIFETIME);
        JwtClaimsSet claims = JwtClaimsSet.builder().subject(user.id().toString()).issuedAt(issuedAt).expiresAt(expiresAt)
                .claim("email", user.email()).claim("roles", java.util.List.of(user.role().name())).build();
        String token = jwtEncoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.RS256).build(), claims)).getTokenValue();
        return new AuthenticationToken(token, expiresAt);
    }
}
