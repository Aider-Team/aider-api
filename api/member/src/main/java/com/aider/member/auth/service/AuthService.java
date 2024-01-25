package com.aider.member.auth.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.aider.coresecurity.security.jwt.JwtProvider;
import com.aider.coresecurity.security.service.UserDetailsImpl;
import com.aider.infra.repository.RedisRefreshTokenRepository;
import com.aider.member.Member;
import com.aider.member.auth.dto.TokenResponseDto;
import com.aider.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final RedisRefreshTokenRepository redisRefreshTokenRepository;
	private final JwtProvider jwtProvider;
	private final MemberRepository memberRepository;

	public TokenResponseDto refreshAccessToken(String refreshToken) {
		Long memberId = redisRefreshTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow();

		Member member = memberRepository.findById(memberId)
			.orElseThrow();

		UserDetailsImpl userDetails = createUserDetails(member);
		String newAccessToken = jwtProvider.createAccessToken(userDetails);
		String newRefreshToken = jwtProvider.createRefreshToken();

		redisRefreshTokenRepository.save(refreshToken, memberId);
		return new TokenResponseDto(newAccessToken, newRefreshToken);
	}

	private UserDetailsImpl createUserDetails(Member member) {
		List<SimpleGrantedAuthority> authorities = member.getGroup()
			.getGroupRoles()
			.stream()
			.map(groupPermission -> new SimpleGrantedAuthority(groupPermission.getRole().getName()))
			.collect(Collectors.toList());

		return UserDetailsImpl.builder()
			.id(member.getId())
			.authorities(authorities)
			.build();
	}
}
