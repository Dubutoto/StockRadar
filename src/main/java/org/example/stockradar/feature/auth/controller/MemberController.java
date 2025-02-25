package org.example.stockradar.feature.auth.controller;

import org.example.stockradar.feature.auth.dto.MemberSignupDto;
import org.example.stockradar.feature.auth.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public String signUp(@RequestBody MemberSignupDto signupDto) {
        memberService.signUp(signupDto);
        return "회원가입이 완료되었습니다.";
    }
}

