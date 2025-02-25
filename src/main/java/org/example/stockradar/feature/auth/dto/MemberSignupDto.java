package org.example.stockradar.feature.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupDto {
    private String memberId;    // 이메일/아이디
    private String memberPw;    // 비밀번호(평문)
    private String userName;    // 사용자명
    private String memberPhone; // 연락처
}