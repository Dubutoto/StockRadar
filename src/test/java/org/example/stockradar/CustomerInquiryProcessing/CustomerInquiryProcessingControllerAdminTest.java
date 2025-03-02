package org.example.stockradar.CustomerInquiryProcessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.example.stockradar.feature.auth.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerInquiryProcessingControllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie regularUserAccessTokenCookie;
    private Cookie regularUserRefreshTokenCookie;

    @BeforeEach
    void setUp() throws Exception {
        // 일반 회원으로 로그인 (관리자가 아닌 계정 사용)
        LoginRequest regularUserLoginRequest = new LoginRequest();
        regularUserLoginRequest.setMemberId("krt8599@naver.com"); // 일반 회원 계정으로 변경
        regularUserLoginRequest.setMemberPw("kim2428");             // 일반 회원 비밀번호로 변경

        MvcResult regularUserLoginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("memberId", regularUserLoginRequest.getMemberId())
                        .param("memberPw", regularUserLoginRequest.getMemberPw()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpServletResponse regularUserResponse = regularUserLoginResult.getResponse();
        jakarta.servlet.http.Cookie[] regularUserCookies = regularUserResponse.getCookies();

        if (regularUserCookies != null) {
            for (jakarta.servlet.http.Cookie cookie : regularUserCookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    regularUserAccessTokenCookie = new Cookie(cookie.getName(), cookie.getValue());
                } else if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    regularUserRefreshTokenCookie = new Cookie(cookie.getName(), cookie.getValue());
                }
            }
        }
    }

    @Test
    @DisplayName("일반 회원은 관리자 페이지에 접근할 수 없다")
    void regularUserCannotAccessAdminPage() throws Exception {
        // 일반 회원으로 관리자 페이지 접근 시도
        mockMvc.perform(get("/customerInquiryprocessing/customerInquiryprocessing")
                        .cookie(regularUserAccessTokenCookie)
                        .cookie(regularUserRefreshTokenCookie))
                .andExpect(status().isForbidden());  // 403 Forbidden 상태 코드 확인
    }
}
