package com.aider.coresecurity.security.config;

import com.aider.coresecurity.security.jwt.JwtFilter;
import com.aider.coresecurity.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final JwtProvider jwtProvider;

    /**
     * 패스워드 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Exception Handler, Filter Method 분기점 나누어야 함.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        httpSecuritySetting(http);
        http
                .authorizeRequests()
                .anyRequest().permitAll();
//                .and()
//                .addFilterBefore(new JwtFilter(jwtProvider), ExceptionTranslationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin(출처)
        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:8080",
                        "http://127.0.0.1:8080",
                        "http://localhost:3000",
                        "http://127.0.0.1:3000"
                )
        );

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE"
                )
        );

        // 허용할 헤더
        configuration.setAllowedHeaders(
                Arrays.asList(
                        "Authorization",
                        "Cache-Control",
                        "Content-Type"
                )
        );

        // 쿠키 및 인증 정보 전송
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void httpSecuritySetting(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // jwt, OAuth 토큰을 사용 -> OAuth의 경우는 이슈가 발생할 수 있음 OAuth 구성할때 체크
            .cors().configurationSource(corsConfigurationSource()) // cors 정책
            .and()
//            .anonymous().disable() // 익명 사용자 접근 제한, 모든 요청이 인증 필요
            .formLogin().disable() // form 기반 로그인을 사용하지 않음.
            .httpBasic().disable() // 기본으로 제공하는 http 사용하지 않음
            .rememberMe().disable() // 토큰 기반이므로 세션 기반의 인증 사용하지 않음
            .headers().frameOptions().disable() // x-Frame-Options 헤더 비활성화, 클릭재킹 공격 관련
            .and()
            .logout().disable() // stateful 하지 않기때문에 필요하지 않음
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 생성을 하지 않음
    }
}
