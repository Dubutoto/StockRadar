package org.example.stockradar.feature.auth.controller;

import org.example.stockradar.feature.auth.dto.MemberSignupDto;
import org.example.stockradar.feature.auth.dto.LoginRequest;
import org.example.stockradar.feature.auth.dto.TokenResponse;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.service.MemberService;
import org.example.stockradar.feature.auth.jwt.JwtTokenProvider;
import org.example.stockradar.feature.auth.jwt.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final PasswordEncoder passwordEncoder;

    // 1) 회원가입
    @PostMapping("/signup")
    public String signUp(@RequestBody MemberSignupDto dto) {
        memberService.signUp(dto);
        return "회원가입이 완료되었습니다.";
    }

    // 2) 로그인 (AccessToken, RefreshToken 발급)
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        // 사용자 조회
        Member member = Optional.ofNullable(memberRepository.findByMemberId(request.getMemberId()))
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getMemberPw(), member.getMemberPw())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
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

        return new TokenResponse(accessToken, refreshToken);
    }

    // 3) 토큰 재발급
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody TokenResponse request) {
        String oldRefreshToken = request.getRefreshToken();
        // 토큰 검증
        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new RuntimeException("유효하지 않은 RefreshToken입니다.");
        }
        // 토큰에서 memberId 추출
        String memberId = jwtTokenProvider.getMemberIdFromToken(oldRefreshToken);

        // Redis에서 저장된 RefreshToken 확인
        String savedRefreshToken = refreshTokenRedisService.getRefreshToken(memberId);
        if (!oldRefreshToken.equals(savedRefreshToken)) {
            throw new RuntimeException("만료되었거나 이미 로그아웃된 RefreshToken입니다.");
        }

        // 새 AccessToken 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
        // 필요 시 RefreshToken도 재발급
        // String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId);
        // refreshTokenRedisService.saveRefreshToken(memberId, newRefreshToken, ...);

        return new TokenResponse(newAccessToken, oldRefreshToken);
    }

    // 4) 로그아웃 (RefreshToken 삭제)
    @PostMapping("/logout")
    public String logout(@RequestBody TokenResponse request) {
        String refreshToken = request.getRefreshToken();
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
            refreshTokenRedisService.deleteRefreshToken(memberId);
        }
        return "로그아웃되었습니다.";
    }
}
