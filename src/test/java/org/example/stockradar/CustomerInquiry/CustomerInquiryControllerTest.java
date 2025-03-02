package org.example.stockradar.CustomerInquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryUserRequestDto;
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

import jakarta.servlet.http.Cookie;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerInquiryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie accessTokenCookie;
    private Cookie refreshTokenCookie;

    @BeforeEach
    public void setup() throws Exception {
        // 로그인 요청 수행
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMemberId("krt8599@naver.com");
        loginRequest.setMemberPw("kim2428");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("memberId", loginRequest.getMemberId())
                        .param("memberPw", loginRequest.getMemberPw()))
                .andExpect(status().is3xxRedirection())  // 리다이렉트 상태 코드 확인
                .andReturn();

        // 응답에서 쿠키 추출
        MockHttpServletResponse response = loginResult.getResponse();
        jakarta.servlet.http.Cookie[] cookies = response.getCookies();

        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    accessTokenCookie = new Cookie(cookie.getName(), cookie.getValue());
                } else if ("REFRESH_TOKEN".equals(cookie.getName())) {
                    refreshTokenCookie = new Cookie(cookie.getName(), cookie.getValue());
                }
            }
        }
    }

    @Test
    @DisplayName("로그인 후 문의 제출 테스트")
    public void submitAfterLogin() throws Exception {
        // 로그인이 성공했는지 확인
        if (accessTokenCookie == null) {
            throw new RuntimeException("로그인 실패: 액세스 토큰이 없습니다.");
        }

        // 문의 데이터 준비
        CustomerInquiryUserRequestDto requestDto = new CustomerInquiryUserRequestDto(
                "테스트 제목",
                "기술 지원",
                "krt8599@naver.com",
                "테스트 문의 내용입니다."
        );

        // 문의 제출 요청 수행
        mockMvc.perform(post("/customerInquiry/submit")
                        .cookie(accessTokenCookie)  // 로그인 시 받은 액세스 토큰 쿠키 사용
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
