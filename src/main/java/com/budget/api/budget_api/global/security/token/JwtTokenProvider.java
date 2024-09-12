package com.budget.api.budget_api.global.security.token;

import com.budget.api.budget_api.global.common.error.ErrorCode;
import com.budget.api.budget_api.global.security.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.Jwts;
import java.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final Long accessExpiration;
    private final Long refreshExpiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-validate-in-seconds}") String accessExpiration,
        @Value("${jwt.refresh-token-validate-in-seconds}") String refreshExpiration) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessExpiration = Long.parseLong(accessExpiration) * 1000;
        this.refreshExpiration = Long.parseLong(refreshExpiration) * 1000;
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token); // JWT 서명 및 유효성 검증
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED, e);
        } catch (MalformedJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("category", String.class);
    }

    public String getAccount(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("account", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
            .get("username", String.class);
    }

    public String createJwt(String category,String account,String username) {
        String jwtCompact = null;

        if(category.equals("access")){
            Jwts.builder()
                .claim("category", category)
                .claim("account", account)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(
                    System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact();

        }else if(category.equals("refresh")){
            Jwts.builder()
                .claim("category", category)
                .claim("account", account)
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(
                    System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey)
                .compact();

        }
        return jwtCompact;
    }
}
