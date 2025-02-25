package org.example.stockradar.feature.auth.service;

import org.example.stockradar.feature.auth.dto.MemberSignupDto;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.entity.Role;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void signUp(MemberSignupDto signupDto) {
        // 1) 아이디(이메일) 중복 체크
        if (memberRepository.findByMemberId(signupDto.getMemberId()) != null) {
            throw new RuntimeException("이미 존재하는 아이디(이메일)입니다.");
        }

        // 2) 비밀번호 해싱
        String hashedPw = passwordEncoder.encode(signupDto.getMemberPw());

        // 3) Member 엔티티 생성
        Member member = new Member();
        member.setMemberId(signupDto.getMemberId());
        member.setMemberPw(hashedPw);
        member.setUserName(signupDto.getUserName());
        member.setMemberPhone(signupDto.getMemberPhone());
        // 권한은 무조건 MEMBER
        member.setRole(Role.MEMBER);

        // 4) DB 저장
        memberRepository.save(member);
    }
}
