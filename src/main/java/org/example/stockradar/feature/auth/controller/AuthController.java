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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;

    // CoolSMS + Redis
    private final CoolsmsService coolsmsService;
    private final RedisTemplate<String, String> redisTemplate;

    // 아이디 찾기 처리 (휴대폰 번호로 검색)
    @PostMapping("/findIdByPhone")
    public String findIdByPhone(@RequestParam("phone") String phone,
                                Model model) {
        // 1) DB에서 동일한 phone을 가진 모든 Member 조회
        List<Member> members = memberRepository.findAllByMemberPhone(phone);

        if (members.isEmpty()) {
            // 가입된 회원 없음
            model.addAttribute("error", "해당 번호로 등록된 아이디(이메일)가 없습니다.");
        } else {
            // 2) 여러 건이면 전부 화면에 표시
            model.addAttribute("foundMembers", members);
        }

        // 다시 idInquiry.html 로 이동
        return "auth/idInquiry";
    }

    /**
     * (1) 휴대폰 인증번호 전송
     */
    @PostMapping("/sendCode")
    public String sendCode(@RequestParam("memberPhone") String memberPhone,
                           @RequestParam("memberId") String memberId,
                           @RequestParam("memberPw") String memberPw,
                           @RequestParam("userName") String userName,
                           Model model) {
        // 폼에서 넘어온 기존 값들 다시 model에 담아주어, signUp.html에서 유지
        model.addAttribute("memberId", memberId);
        model.addAttribute("memberPw", memberPw);
        model.addAttribute("userName", userName);
        model.addAttribute("memberPhone", memberPhone);

        // 혹시 모를 콤마 체크 (디버깅)
        // System.out.println("DEBUG: memberId=" + memberId);

        // 폰번호 검증
        if (memberPhone == null || memberPhone.isEmpty()) {
            model.addAttribute("error", "휴대폰 번호를 입력해주세요.");
            return "auth/signUp";
        }

        // 인증번호 생성 (6자리)
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        // Redis에 저장 (유효기간 3분)
        redisTemplate.opsForValue().set("PHONE_VERIFY:" + memberPhone, code, Duration.ofMinutes(3));

        // 실제 문자 전송
        String message = "[StockRadar] 인증번호: " + code;
        coolsmsService.sendSms(memberPhone, message);

        model.addAttribute("success", "인증번호를 전송했습니다. (3분 내 입력)");
        return "auth/signUp"; // signUp.html 로 다시 이동
    }

    /**
     * (2) 휴대폰 인증번호 확인
     */
    @PostMapping("/checkCode")
    public String checkCode(@RequestParam("memberPhone") String memberPhone,
                            @RequestParam("verifyCode") String verifyCode,
                            @RequestParam("memberId") String memberId,
                            @RequestParam("memberPw") String memberPw,
                            @RequestParam("userName") String userName,
                            HttpServletRequest request,
                            Model model) {

        // 기존 입력값 복원
        model.addAttribute("memberId", memberId);
        model.addAttribute("memberPw", memberPw);
        model.addAttribute("userName", userName);
        model.addAttribute("memberPhone", memberPhone);

        // 폰번호, 인증번호 기본 검증
        if (memberPhone == null || memberPhone.isEmpty()) {
            model.addAttribute("error", "휴대폰 번호가 비어있습니다.");
            return "auth/signUp";
        }
        if (verifyCode == null || verifyCode.isEmpty()) {
            model.addAttribute("error", "인증번호를 입력해주세요.");
            return "auth/signUp";
        }

        // Redis에서 저장된 인증번호 확인
        String savedCode = redisTemplate.opsForValue().get("PHONE_VERIFY:" + memberPhone);
        if (savedCode == null) {
            model.addAttribute("error", "인증번호가 만료되었거나 전송되지 않았습니다.");
            return "auth/signUp";
        }
        if (!savedCode.equals(verifyCode)) {
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            return "auth/signUp";
        }

        // 인증 성공
        redisTemplate.delete("PHONE_VERIFY:" + memberPhone);
        request.getSession().setAttribute("phoneVerified", true);

        // 인증완료 표시 (템플릿에서 disabled 처리 등에 사용)
        model.addAttribute("phoneVerified", true);
        // 인증번호를 다시 표시(흑백 비활성용)
        model.addAttribute("verifyCode", verifyCode);

        model.addAttribute("success", "휴대폰 인증이 완료되었습니다!");
        return "auth/signUp";
    }

    /**
     * (3) 회원가입
     */
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

    /**
     * (4) 로그인 (JWT)
     */
    @PostMapping("/login")
    public String login(LoginRequest request,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {

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

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getMemberId());

        // Redis에 RefreshToken 저장
        refreshTokenRedisService.saveRefreshToken(
                member.getMemberId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidity()
        );

        // 쿠키에 저장
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return "redirect:/main";
    }

    /**
     * (5) 토큰 재발급
     */
    @ResponseBody
    @PostMapping("/refresh")
    public String refresh(@CookieValue("REFRESH_TOKEN") String oldRefreshToken,
                          HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new RuntimeException("유효하지 않은 RefreshToken입니다.");
        }
        String memberId = jwtTokenProvider.getMemberIdFromToken(oldRefreshToken);

        String savedRefreshToken = refreshTokenRedisService.getRefreshToken(memberId);
        if (!oldRefreshToken.equals(savedRefreshToken)) {
            throw new RuntimeException("만료되었거나 이미 로그아웃된 RefreshToken입니다.");
        }

        // 새 AccessToken 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);

        // 쿠키 갱신
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        response.addCookie(accessCookie);

        return "새 AccessToken 발급 완료";
    }

    /**
     * (6) 로그아웃
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("REFRESH_TOKEN") String refreshToken,
                         HttpServletResponse response) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
            refreshTokenRedisService.deleteRefreshToken(memberId);
        }
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
        accessCookie.setMaxAge(0);
        accessCookie.setPath("/");

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return "redirect:/login";
    }
}