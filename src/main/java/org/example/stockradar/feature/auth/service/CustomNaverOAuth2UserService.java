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
public class CustomNaverOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) 네이버에서 사용자 정보 가져오기
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2) 네이버 응답 구조: {"resultcode":"00","message":"success","response":{...}}
        Map<String, Object> originalAttributes = oAuth2User.getAttributes();
        Map<String, Object> responseMap = (Map<String, Object>) originalAttributes.get("response");
        if (responseMap == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_response"), "Naver response is null");
        }

        // 3) {"id":"1234","email":"xxx@naver.com","name":"홍길동", ...}
        String id = (String) responseMap.get("id");
        String email = (String) responseMap.get("email");
        String name = (String) responseMap.get("name");

        // 이메일이 없으면 "naver_1234" 형태로 대체
        if (email == null) {
            email = "naver_" + id;
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
                "response" // userNameAttributeName (Naver는 "response")
        );
    }
}
