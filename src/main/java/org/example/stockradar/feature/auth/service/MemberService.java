package org.example.stockradar.feature.auth.service;

import org.example.stockradar.feature.auth.dto.MemberSignupDto;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.entity.Role;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signUp(MemberSignupDto signupDto) {
        // 중복 체크
        if (memberRepository.findByMemberId(signupDto.getMemberId()) != null) {
            throw new RuntimeException("이미 존재하는 아이디(이메일)입니다.");
        }

        // 비밀번호 해싱
        String hashedPw = passwordEncoder.encode(signupDto.getMemberPw());

        // 엔티티 생성
        Member member = new Member(
                signupDto.getMemberId(),
                hashedPw,
                signupDto.getUserName(),
                signupDto.getMemberPhone(),
                Role.MEMBER  // 회원가입 시 무조건 MEMBER
        );

        // DB 저장
        memberRepository.save(member);
    }
}