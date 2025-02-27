package org.example.stockradar.feature.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.entity.Role;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomDiscordOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) Discord에서 사용자 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2) Discord 응답 예: { "id":"1234567", "username":"abc", "email":"...", ... }
        Map<String, Object> originalAttributes = oAuth2User.getAttributes();

        String id = (String) originalAttributes.get("id");        // 디스코드 고유 ID
        String email = (String) originalAttributes.get("email");  // scope 설정에 따라 null 가능
        String username = (String) originalAttributes.get("username");

        // 이메일이 없으면 "discord_1234567" 같은 형태로 대체
        if (email == null) {
            email = "discord_" + id;
        }

        // 3) DB에서 사용자 조회 or 생성
        Member member = memberRepository.findByMemberId(email);
        if (member == null) {
            member = new Member(email, "", username, "", Role.MEMBER);
            memberRepository.save(member);
        }

        // 4) 읽기 전용 Map이므로, 새로 복사 후 "memberId" 삽입
        Map<String, Object> newAttributes = new HashMap<>(originalAttributes);
        newAttributes.put("memberId", email);

        // 5) ROLE_MEMBER 권한
        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_MEMBER"),
                newAttributes,
                "id" // userNameAttributeName (Discord는 "id")
        );
    }
}
