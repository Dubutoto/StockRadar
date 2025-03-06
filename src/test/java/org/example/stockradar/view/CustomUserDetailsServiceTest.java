package org.example.stockradar.view;

import org.example.stockradar.feature.auth.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.feature.auth.entity.Role;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getMemberRole_WithValidMemberId_ReturnsCorrectRole() {
        // Arrange
        String memberId = "krt8599@naver.com";
        Member mockMember = new Member();
        mockMember.setMemberId(memberId);
        mockMember.setMemberPw("kim2428");
        mockMember.setRole(Role.ADMIN); // Assuming ADMIN is the role

        when(memberRepository.findByMemberId(memberId)).thenReturn(mockMember);

        // Act
        String role = customUserDetailsService.getMemberRole(memberId);

        // Assert
        assertEquals("ADMIN", role);
        verify(memberRepository, times(1)).findByMemberId(memberId);
        System.out.println(role);
    }

    @Test
    void getMemberRole_WithInvalidMemberId_ThrowsUsernameNotFoundException() {
        // Arrange
        String invalidMemberId = "nonexistent@example.com";
        when(memberRepository.findByMemberId(invalidMemberId)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.getMemberRole(invalidMemberId);
        });
        verify(memberRepository, times(1)).findByMemberId(invalidMemberId);
    }
}

