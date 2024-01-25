package com.aider.coresecurity.security.jwt;

import com.aider.coresecurity.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final String AUTHENTICATION_CLAIM_NAME = "roles";

    @Value("${jwt.secret-key}")
    private String accessSecret;

    @Value("${jwt.expiry-seconds}")
    private int expirySeconds;

    /**
     * 토큰 생성
     */
    public String createToken(UserDetailsImpl userDetails) {
        Instant now = Instant.now();
        Date expiration = Date.from(now.plusSeconds(expirySeconds));
        SecretKey key = extractSecretKey();

        StringBuilder roles = new StringBuilder();
        if(userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
            roles.append(
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(", "))
            );
        }

        return Jwts.builder()
                .claim("id", userDetails.getId())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(expiration)
                .claim(AUTHENTICATION_CLAIM_NAME, roles.toString())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 권한 체크
     */
    public Authentication toAuthentication(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(extractSecretKey())
                .build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        Object roles = claims.get(AUTHENTICATION_CLAIM_NAME);
        List<SimpleGrantedAuthority> authorities = null;
        if(roles != null && roles.toString().trim().isEmpty()) {
            authorities = Arrays.stream(roles.toString().split(","))
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        UserDetails user = UserDetailsImpl.builder()
                .id(claims.get("id", Long.class))
                .email(claims.getSubject())
                .password(null)
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    /**
     * 토큰 검증
     */
    public Boolean validate(String token) {
        try{
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(extractSecretKey())
                    .build();
            jwtParser.parse(token);

            return true;
        }catch (JwtException e) {
            return false;
        }
    }

    /**
     * SecretKey 추출
     */
    private SecretKey extractSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
    }
}
