package org.example.stockradar.CustomerInquiry;

import org.example.stockradar.feature.CustomerInquiry.controller.CustomerInquiryController;
import org.example.stockradar.feature.CustomerInquiry.service.CustomerInquiryService;
import org.example.stockradar.feature.auth.jwt.JwtAuthenticationFilter;
import org.example.stockradar.feature.auth.jwt.JwtTokenProvider;
import org.example.stockradar.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CustomerInquiryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
public class CustomerInquiryControllerTwoTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        // JwtTokenProvider 모킹
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return mock(JwtTokenProvider.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerInquiryService customerInquiryService;

    @MockitoBean
    private Authentication authentication;

    @Test
    @DisplayName("인증된 사용자의 API 체크 요청 테스트")
    public void testCustomerInquiryWithAuthenticatedUser() throws Exception {
        // Given
        String memberId = "testUser";
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(memberId);

        // When & Then
        mockMvc.perform(get("/customerInquiry/api/check")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("인증 성공"))
                .andExpect(jsonPath("$.redirectUrl").value("/customerInquiry/customerInquiry"));

        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 API 체크 요청 테스트")
    public void testCustomerInquiryWithUnauthenticatedUser() throws Exception {
        // Given
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/customerInquiry/api/check")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(ErrorCode.UNAUTHORIZED.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getErrorMessage()))
                .andExpect(jsonPath("$.redirectUrl").value("/login"));

        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, never()).getName();
    }

    @Test
    @DisplayName("인증 정보가 없는 API 체크 요청 테스트")
    public void testCustomerInquiryWithNoAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/customerInquiry/api/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(ErrorCode.UNAUTHORIZED.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getErrorMessage()))
                .andExpect(jsonPath("$.redirectUrl").value("/login"));
    }

    @Test
    @DisplayName("AuthException이 발생하는 경우 테스트")
    public void testCustomerInquiryWithAuthException() throws Exception {
        // Given
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenThrow(RuntimeException.class);

        // When & Then
        mockMvc.perform(get("/customerInquiry/api/check")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(ErrorCode.UNAUTHORIZED.getHttpStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED.getErrorMessage()))
                .andExpect(jsonPath("$.redirectUrl").value("/login"));

        verify(authentication, times(1)).isAuthenticated();
    }
}
