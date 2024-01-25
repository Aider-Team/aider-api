package com.aider.member.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aider.member.auth.dto.TokenResponseDto;
import com.aider.member.auth.service.AuthService;
import com.aider.response.AiderResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/reissue-token")
	public ResponseEntity<AiderResponse> refreshAccessToken(@CookieValue String refreshToken) {
		TokenResponseDto tokenResponse = authService.refreshAccessToken(refreshToken);
		return ResponseEntity.ok(new AiderResponse(tokenResponse));
	}
}
