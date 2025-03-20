package org.example.stockradar.feature.auth.service;

import org.example.stockradar.feature.auth.dto.MemberSignupDto;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.entity.Role;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.notification.entity.NotificationChannel;
import org.example.stockradar.feature.notification.entity.NotificationSetting;
import org.example.stockradar.feature.notification.repository.NotificationSettingRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationSettingRepository notificationSettingRepository;

    // 회원가입
    public void signUp(MemberSignupDto signupDto) {
        // 중복 체크
        if (memberRepository.findByMemberId(signupDto.getMemberId()) != null) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
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

        // 회원 저장
        Member savedMember = memberRepository.save(member);

        // 기본 알림 설정 생성
        List<NotificationSetting> defaultSettings = new ArrayList<>();
        defaultSettings.add(NotificationSetting.createDefaultSetting(savedMember, NotificationChannel.EMAIL));
        defaultSettings.add(NotificationSetting.createDefaultSetting(savedMember, NotificationChannel.SMS));
        defaultSettings.add(NotificationSetting.createDefaultSetting(savedMember, NotificationChannel.DISCORD));

        // 기본 알림 설정 저장
        notificationSettingRepository.saveAll(defaultSettings);
    }
}