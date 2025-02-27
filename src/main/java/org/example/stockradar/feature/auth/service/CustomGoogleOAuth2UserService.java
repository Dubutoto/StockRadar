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
public class CustomGoogleOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) 구글에서 사용자 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2) 구글 응답 예시:
        // {
        //   "sub": "123456789012345678901",
        //   "email": "example@gmail.com",
        //   "email_verified": true,
        //   "name": "홍길동",
        //   "picture": "https://...",
        //   ...
        // }
        Map<String, Object> originalAttributes = oAuth2User.getAttributes();

        // 3) 구글 식별값
        String sub = (String) originalAttributes.get("sub");       // 구글 고유 ID
        String email = (String) originalAttributes.get("email");   // 사용자가 동의했으면 존재, 없으면 null
        String name = (String) originalAttributes.get("name");     // 사용자 이름

        // 이메일이 null이면 "google_{sub}"로 대체
        if (email == null) {
            email = "google_" + sub;
        }

        // 4) DB 조회 or 생성
        Member member = memberRepository.findByMemberId(email);
        if (member == null) {
            member = new Member(email, "", name, "", Role.MEMBER);
            memberRepository.save(member);
        }

        // 5) 새 맵 복사 후 "memberId" 삽입
        Map<String, Object> newAttributes = new HashMap<>(originalAttributes);
        newAttributes.put("memberId", email);

        // 6) ROLE_MEMBER 권한
        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_MEMBER"),
                newAttributes,
                "sub" // userNameAttributeName (구글은 "sub"를 식별자로 쓰지만, 크게 상관없음)
        );
    }
}
