package org.example.stockradar.feature.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.dto.LoginRequest;
import org.example.stockradar.feature.auth.dto.MemberSignupDto;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.jwt.JwtTokenProvider;
import org.example.stockradar.feature.auth.jwt.RefreshTokenRedisService;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.CoolsmsService;
import org.example.stockradar.feature.auth.service.MemberService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final CoolsmsService coolsmsService;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/findIdByPhone")
    public String findIdByPhone(@RequestParam("phone") String phone, Model model) {
        List<Member> members = memberRepository.findAllByMemberPhone(phone);
        if (members.isEmpty()) {
            model.addAttribute("error", "해당 번호로 등록된 아이디(이메일)가 없습니다.");
        } else {
            model.addAttribute("foundMembers", members);
        }
        return "auth/idInquiry";
    }

    @PostMapping("/sendCode")
    public String sendCode(@RequestParam("memberPhone") String memberPhone, @RequestParam("memberId") String memberId,
                           @RequestParam("memberPw") String memberPw, @RequestParam("userName") String userName,
                           Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("memberPw", memberPw);
        model.addAttribute("userName", userName);
        model.addAttribute("memberPhone", memberPhone);

        if (memberPhone.isEmpty()) {
            model.addAttribute("error", "휴대폰 번호를 입력해주세요.");
            return "auth/signUp";
        }

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        redisTemplate.opsForValue().set("PHONE_VERIFY:" + memberPhone, code, Duration.ofMinutes(3));
        coolsmsService.sendSms(memberPhone, "[StockRadar] 인증번호: " + code);

        model.addAttribute("success", "인증번호를 전송했습니다. (3분 내 입력)");
        return "auth/signUp";
    }

    @PostMapping("/checkCode")
    public String checkCode(@RequestParam("memberPhone") String memberPhone, @RequestParam("verifyCode") String verifyCode,
                            @RequestParam("memberId") String memberId, @RequestParam("memberPw") String memberPw,
                            @RequestParam("userName") String userName, HttpServletRequest request, Model model) {
        model.addAttribute("memberId", memberId);
        model.addAttribute("memberPw", memberPw);
        model.addAttribute("userName", userName);
        model.addAttribute("memberPhone", memberPhone);

        String savedCode = redisTemplate.opsForValue().get("PHONE_VERIFY:" + memberPhone);
        if (savedCode == null || !savedCode.equals(verifyCode)) {
            model.addAttribute("error", "인증번호가 일치하지 않거나 만료되었습니다.");
            return "auth/signUp";
        }
        redisTemplate.delete("PHONE_VERIFY:" + memberPhone);
        request.getSession().setAttribute("phoneVerified", true);
        model.addAttribute("success", "휴대폰 인증이 완료되었습니다!");
        return "auth/signUp";
    }

    @PostMapping("/signup")
    public String signup(MemberSignupDto dto,
                         HttpServletRequest request,
                         Model model) {
        try {
            // 휴대폰 인증 여부 확인
            Boolean phoneVerified = (Boolean) request.getSession().getAttribute("phoneVerified");
            if (phoneVerified == null || !phoneVerified) {
                model.addAttribute("error", "휴대폰 인증이 완료되지 않았습니다.");

                // 기존 입력값 유지
                model.addAttribute("memberId", dto.getMemberId());
                model.addAttribute("memberPw", dto.getMemberPw());
                model.addAttribute("userName", dto.getUserName());
                model.addAttribute("memberPhone", dto.getMemberPhone());
                return "auth/signUp";
            }

            // 실제 회원가입 로직
            memberService.signUp(dto);

            // 가입 완료 후 세션에서 제거
            request.getSession().removeAttribute("phoneVerified");

            model.addAttribute("msg", "회원가입이 완료되었습니다.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            // 가입 중 오류 발생 시
            model.addAttribute("error", e.getMessage());

            // 기존 입력값 유지
            model.addAttribute("memberId", dto.getMemberId());
            model.addAttribute("memberPw", dto.getMemberPw());
            model.addAttribute("userName", dto.getUserName());
            model.addAttribute("memberPhone", dto.getMemberPhone());
            return "auth/signUp";
        }
    }

    @PostMapping("/login")
    public String login(LoginRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Optional<Member> memberOptional = Optional.ofNullable(memberRepository.findByMemberId(request.getMemberId()));
        if (memberOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 아이디입니다.");
            return "redirect:/login";
        }

        Member member = memberOptional.get();
        if (!passwordEncoder.matches(request.getMemberPw(), member.getMemberPw())) {
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "redirect:/login";
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        String sessionId = UUID.randomUUID().toString();

        //Access 토큰 쿠키에 저장
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(30 * 60); // 30분 유지

        response.addCookie(accessCookie);

        refreshTokenRedisService.saveRefreshToken(sessionId, refreshToken, jwtTokenProvider.getRefreshTokenValidity());

        //SESSION_ID 도 쿠키에 저장
        response.addHeader("Set-Cookie", "SESSION_ID=" + sessionId + "; HttpOnly; Path=/");
        return "redirect:/";
    }

    @ResponseBody
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(value = "SESSION_ID", required = false) String sessionId,
                                                       HttpServletResponse response) {
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 없습니다."));
        }

        String refreshToken = refreshTokenRedisService.getRefreshToken(sessionId);
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "유효하지 않은 RefreshToken입니다."));
        }

        // 새 AccessToken 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(jwtTokenProvider.getMemberIdFromToken(refreshToken));

        // 새 AccessToken을 쿠키에 저장
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(30 * 60); // 30분 유지 -> 클라이언트 계층에서 28분 지날때 다시 발급 되게 할거임

        response.addCookie(accessCookie);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @GetMapping("/logout")
    public String logout(
            @CookieValue(value = "SESSION_ID", required = false) String sessionId,
            HttpServletResponse response) {

        // Redis에 저장된 refresh token 삭제
        if (sessionId != null) {
            refreshTokenRedisService.deleteRefreshToken(sessionId);
        }

        // SESSION_ID 쿠키 삭제 (로그인 시와 동일한 옵션 사용)
        Cookie sessionCookie = new Cookie("SESSION_ID", null);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);
        sessionCookie.setHttpOnly(true);
        response.addCookie(sessionCookie);

        // ACCESS_TOKEN 쿠키 삭제 (로그인 시 사용한 경우)
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setHttpOnly(true);
        response.addCookie(accessCookie);

        return "redirect:/login";
    }
}
