package org.example.stockradar.config;

import org.example.stockradar.feature.auth.jwt.JwtAuthenticationFilter;
import org.example.stockradar.feature.auth.service.CustomUserDetailsService;
import org.example.stockradar.feature.auth.service.OAuth2LoginSuccessHandler;
import org.example.stockradar.feature.auth.service.RoutingOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RoutingOAuth2UserService routingOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // AuthenticationManager
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        AuthenticationManager authenticationManager = authBuilder.build();

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                // 로그인/회원가입/리소스 파일은 모두 허용
                .requestMatchers("/login", "/signup", "/auth/**", "/css/**", "/js/**", "/images/**", "/customerInquiry/**")).permitAll()
                // 나머지는 인증 필요
                .anyRequest().authenticated()
                )
        // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // 커스텀 로그인 페이지
                .userInfoEndpoint(userInfo -> userInfo.userService(routingOAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler)
        )
                // JWT 필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}