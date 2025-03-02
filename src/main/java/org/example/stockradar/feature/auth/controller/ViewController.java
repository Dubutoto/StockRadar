package org.example.stockradar.feature.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    // 로그인 폼
    @GetMapping("/login")
    public String loginForm() {
        return "auth/signIn"; // login.html (Thymeleaf)
    }

    // 회원가입 폼
    @GetMapping("/signup")
    public String signupForm() {
        return "auth/signUp"; // signup.html
    }

    // 메인 페이지
    @GetMapping("/main")
    public String mainPage() {
        return "main"; // main.html
    }
}
