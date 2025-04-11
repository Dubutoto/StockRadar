package org.example.stockradar.config;

import org.example.stockradar.feature.auth.jwt.JwtAuthenticationFilter;
import org.example.stockradar.feature.auth.service.CustomUserDetailsService;
import org.example.stockradar.feature.auth.service.OAuth2LoginSuccessHandler;
import org.example.stockradar.feature.auth.service.RoutingOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RoutingOAuth2UserService routingOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager (필요 시)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable())  // 기본 캐시 제어 헤더 비활성화
                        .frameOptions(frame -> frame.deny())     // 프레임 옵션 유지
                )

                // .authenticationManager(authenticationManager)

                // -- feature/40 쪽 authorizeHttpRequests --
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login", "/signup", "/auth/**",
                                "/css/**", "/js/**", "/images/**",
                                "/customerInquiry/**", "/board/**",
                                "/assets/**", "/main", "/", "/auth/idInquiry", "/idInquiry",
                                "/password/**","/product/**",
                                "/main/check","/common/**", "comment/read",
                                "/monitoring/**","/refresh","auth/refresh", "notification/**"

                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2

                        .loginPage("/login") // 커스텀 로그인 페이지
                        .userInfoEndpoint(userInfo -> userInfo.userService(routingOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}