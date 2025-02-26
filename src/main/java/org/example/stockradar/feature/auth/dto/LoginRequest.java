package org.example.stockradar.feature.auth.dto;

import lombok.Getter;
import lombok.Setter;

// 로그인 DTO
@Getter
@Setter
public class LoginRequest {
    private String memberId;   // 이메일/아이디
    private String memberPw;   // 비밀번호(평문)
}
