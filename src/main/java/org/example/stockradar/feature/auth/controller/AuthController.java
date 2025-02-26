package org.example.stockradar.feature.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.dto.MemberSignupDto;
import org.example.stockradar.feature.auth.dto.LoginRequest;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.MemberService;
import org.example.stockradar.feature.auth.jwt.JwtTokenProvider;
import org.example.stockradar.feature.auth.jwt.RefreshTokenRedisService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller  // ★ RestController → Controller 로 변경
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(MemberSignupDto dto, Model model) {
        try {
            memberService.signUp(dto);
            model.addAttribute("msg", "회원가입이 완료되었습니다.");
            // 회원가입 성공 → 로그인 페이지로 리다이렉트
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            // 실패 시 다시 회원가입 폼
            return "signup";
        }
    }

    // 로그인 처리 (폼 submit)
    @PostMapping("/login")
    public String login(LoginRequest request, HttpServletResponse response, Model model) {
        // DB에서 사용자 조회
        Member member = Optional.ofNullable(memberRepository.findByMemberId(request.getMemberId()))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getMemberPw(), member.getMemberPw())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "login";  // 로그인 폼으로 다시 이동
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getMemberId());

        // Redis에 RefreshToken 저장
        refreshTokenRedisService.saveRefreshToken(
                member.getMemberId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidity()
        );

        // 쿠키에 AccessToken, RefreshToken 저장
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        // 필요 시 Secure, SameSite 설정

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 로그인 성공 → 메인 페이지로 리다이렉트
        return "redirect:/main";  // ★ 문자열 그대로 브라우저에 보여주지 않고, 302 리다이렉트로 처리
    }

    // 토큰 재발급 (JSON 응답이 필요하므로 @ResponseBody 사용)
    @ResponseBody
    @PostMapping("/refresh")
    public String refresh(@CookieValue("REFRESH_TOKEN") String oldRefreshToken,
                          HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new RuntimeException("유효하지 않은 RefreshToken입니다.");
        }
        String memberId = jwtTokenProvider.getMemberIdFromToken(oldRefreshToken);

        // Redis에서 저장된 RefreshToken 확인
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

        return "새 AccessToken 발급 완료";  // JSON이나 문자열로 응답
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(@CookieValue("REFRESH_TOKEN") String refreshToken,
                         HttpServletResponse response) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
            // Redis에서 RefreshToken 삭제
            refreshTokenRedisService.deleteRefreshToken(memberId);
        }
        // 쿠키 만료
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
        accessCookie.setMaxAge(0);
        accessCookie.setPath("/");

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 로그아웃 후 로그인 페이지로 리다이렉트
        return "redirect:/login";
    }
}
