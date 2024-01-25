package com.aider.infra.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository {

	private static final Long EXPIRED_TIME = 259200L;
	private final RedisTemplate redisTemplate;

	public void save(String refreshToken, Long memberId) {
		ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
		Duration expireDuration = Duration.ofMillis(EXPIRED_TIME);
		valueOperations.set(refreshToken, memberId, expireDuration);
		log.info("set Refresh with key = {} , value = {} , expireDuration = {}", refreshToken, memberId,
			expireDuration);
	}

	public Optional<Long> findByRefreshToken(String refreshToken) {
		ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
		Long memberId = valueOperations.get(refreshToken);
		return Optional.ofNullable(memberId);
	}
}
