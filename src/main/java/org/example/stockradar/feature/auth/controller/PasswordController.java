package org.example.stockradar.feature.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.SmtpMailService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {

    private final MemberRepository memberRepository;
    private final SmtpMailService smtpMailService;
    private final RedisTemplate<String, String> redisTemplate;

    // ★ Inject the PasswordEncoder to hash the new password
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 찾기 폼(입력) - GET
     *  - /password/pwInquiry (GET)
     */
    @GetMapping("/pwInquiry")
    public String pwInquiryForm() {
        return "auth/pwInquiry";
    }

    /**
     * 비밀번호 찾기 처리 - POST
     *  - /password/pwInquiry (POST)
     */
    @PostMapping("/pwInquiry")
    public String processPwInquiry(@RequestParam("reset-email") String email,
                                   Model model) {
        // 1) 이메일 존재 여부 확인
        Member member = memberRepository.findByMemberId(email);
        if (member == null) {
            model.addAttribute("error", "해당 이메일로 가입된 회원이 존재하지 않습니다.");
            return "auth/pwInquiry";
        }

        // 2) 토큰 생성 & Redis에 저장
        String token = UUID.randomUUID().toString();
        // 10분 만료 예시
        redisTemplate.opsForValue().set("PW_RESET:" + token, email, Duration.ofMinutes(10));

        // 3) 메일 발송
        // 예) http://localhost:8080/password/resetPwForm?token=xxx
        String resetLink = "http://localhost:8080/password/resetPwForm?token=" + token;
        String subject = "[StockRadar] 비밀번호 재설정 안내";
        String text = "아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" + resetLink;

        smtpMailService.sendMail(email, subject, text);

        model.addAttribute("msg", "해당 이메일로 비밀번호 재설정 링크를 전송했습니다. 10분 내에 진행해주세요.");
        return "auth/pwInquiry";
    }

    /**
     * 비밀번호 재설정 폼 - GET
     *  - /password/resetPwForm?token=xxx
     */
    @GetMapping("/resetPwForm")
    public String resetPwForm(@RequestParam("token") String token, Model model) {
        // 1) 토큰 유효성 확인
        String email = redisTemplate.opsForValue().get("PW_RESET:" + token);
        if (email == null) {
            model.addAttribute("error", "유효하지 않거나 만료된 링크입니다.");
            return "auth/pwInquiry";
        }

        // 2) 모델에 token 추가 -> 폼에서 hidden으로 전송
        model.addAttribute("token", token);
        return "auth/resetPwForm";
    }

    /**
     * 비밀번호 재설정 처리 - POST
     *  - /password/resetPw
     */
    @PostMapping("/resetPw")
    public String resetPw(@RequestParam("token") String token,
                          @RequestParam("newPassword") String newPassword,
                          @RequestParam("newPasswordConfirm") String newPasswordConfirm,
                          Model model) {

        // 1) 토큰 유효성
        String email = redisTemplate.opsForValue().get("PW_RESET:" + token);
        if (email == null) {
            model.addAttribute("error", "유효하지 않거나 만료된 링크입니다.");
            return "auth/pwInquiry";
        }

        // 2) 비밀번호 확인
        if (!newPassword.equals(newPasswordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("token", token);
            return "auth/resetPwForm";
        }

        // 3) 회원 조회 후 변경
        Member member = memberRepository.findByMemberId(email);
        if (member == null) {
            model.addAttribute("error", "회원 정보를 찾을 수 없습니다.");
            return "auth/pwInquiry";
        }

        // 4) 새 비밀번호를 PasswordEncoder로 해시하여 저장
        member.setMemberPw(passwordEncoder.encode(newPassword));
        memberRepository.save(member);

        // 5) Redis 토큰 삭제
        redisTemplate.delete("PW_RESET:" + token);

        model.addAttribute("msg", "비밀번호가 재설정되었습니다. 새 비밀번호로 로그인해주세요.");
        return "auth/signIn";
    }
}

