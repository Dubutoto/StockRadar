package org.example.stockradar.CustomerInquiryProcessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.example.stockradar.feature.auth.dto.LoginRequest;
import org.example.stockradar.feature.customerInquiryprocessing.dto.CustomerInquiryProcessingResponseDto;
import org.example.stockradar.feature.customerInquiryprocessing.service.CustomerInquiryProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerInquiryProcessingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerInquiryProcessingService service;

    private Cookie adminAccessTokenCookie;
    private Cookie adminRefreshTokenCookie;

    @BeforeEach
    void setUp() throws Exception {
        // 관리자 계정으로 로그인 (krt8599@naver.com이 관리자 권한을 가지고 있음)
        LoginRequest adminLoginRequest = new LoginRequest();
        adminLoginRequest.setMemberId("krt8599@naver.com");
        adminLoginRequest.setMemberPw("kim2428");

        MvcResult adminLoginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("memberId", adminLoginRequest.getMemberId())
                        .param("memberPw", adminLoginRequest.getMemberPw()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpServletResponse adminResponse = adminLoginResult.getResponse();
        jakarta.servlet.http.Cookie[] adminCookies = adminResponse.getCookies();

        if (adminCookies != null) {
            for (jakarta.servlet.http.Cookie cookie : adminCookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    adminAccessTokenCookie = new Cookie(cookie.getName(), cookie.getValue());
                } else if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    adminRefreshTokenCookie = new Cookie(cookie.getName(), cookie.getValue());
                }
            }
        }
    }

    @Test
    @DisplayName("관리자는 상태가 0인 고객문의 목록을 조회할 수 있다")
    void adminCanViewCustomerInquiries() throws Exception {
        // given
        List<CustomerInquiryProcessingResponseDto> mockInquiries = Arrays.asList(
                new CustomerInquiryProcessingResponseDto(),
                new CustomerInquiryProcessingResponseDto()
        );

        when(service.searchStatus()).thenReturn(mockInquiries);


        // when & then
        mockMvc.perform(get("/customerInquiryprocessing/customerInquiryprocessing")
                        .cookie(adminAccessTokenCookie)
                        .cookie(adminRefreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(view().name("customerInquiryprocessing/customerInquiryprocessing"))
                .andExpect(model().attribute("inquiries", mockInquiries))
                .andExpect(model().attribute("inquiryCount", 2));
    }
}
