package org.example.stockradar.feature.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoutingOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final CustomNaverOAuth2UserService customNaverOAuth2UserService;
    private final CustomDiscordOAuth2UserService customDiscordOAuth2UserService;
    private final CustomGoogleOAuth2UserService customGoogleOAuth2UserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("naver".equals(registrationId)) {
            return customNaverOAuth2UserService.loadUser(userRequest);
        } else if ("discord".equals(registrationId)) {
            return customDiscordOAuth2UserService.loadUser(userRequest);
        } else if ("google".equals(registrationId)) {
            return customGoogleOAuth2UserService.loadUser(userRequest);
        } else {
            throw new OAuth2AuthenticationException(new OAuth2Error("unsupported_registration_id"),
                    "Unsupported OAuth2 provider: " + registrationId);
        }
    }
}
