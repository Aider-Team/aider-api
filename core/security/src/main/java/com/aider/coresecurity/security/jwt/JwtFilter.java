package com.aider.coresecurity.security.jwt;

import static org.springframework.util.StringUtils.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final String AUTHENTICATION_HEADER = "Authorization";
	private final String AUTHENTICATION_SCHEME = "Bearer ";

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try {
			String accessToken = extractToken(request);
			if (hasText(accessToken) && jwtProvider.validate(accessToken)) {
				SecurityContextHolder.getContext()
					.setAuthentication(jwtProvider.toAuthentication(accessToken));
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			log.warn(">>> Jwt Token Expired !!!");
			throw new CredentialsExpiredException("토큰의 유효기간이 만료되었습니다.", e);
		} catch (Exception e) {
			log.warn(">>> Failed Token Authenticate !!!");
			throw new BadCredentialsException("토큰 인증에 실패하였습니다.");
		}
	}

	/**
	 * 토큰 추출
	 */
	private String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHENTICATION_HEADER);

		if (hasText(bearerToken) && bearerToken.startsWith(AUTHENTICATION_SCHEME)) {
			return bearerToken.substring(AUTHENTICATION_SCHEME.length());
		}

		throw new AuthenticationCredentialsNotFoundException("토큰이 존재하지 않습니다.");
	}
}
