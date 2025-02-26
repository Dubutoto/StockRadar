package org.example.stockradar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화 (개발 환경에서만)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // 모든 요청에 대해 접근 허용
                )
                .formLogin(login -> login.disable()) // 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()); // HTTP 기본 인증 비활성화

        return http.build();
    }
}