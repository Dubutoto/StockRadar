package org.example.stockradar.feature.auth.service;


import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + memberId);
        }
        // 권한은 ROLE_ 접두사
        String roleName = "ROLE_" + member.getRole().name();

        return new User(
                member.getMemberId(),
                member.getMemberPw(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
