package org.example.stockradar.CustomerInquiry;

import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryUserRequestDto;
import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.CustomerInquiry.repository.CustomerInquiryRepository;
import org.example.stockradar.feature.CustomerInquiry.service.CustomerInquiryService;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@AutoConfigureMockMvc
public class CustomerInquiryServiceTest {

    private final CustomerInquiryRepository repository = Mockito.mock(CustomerInquiryRepository.class);
    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private final CustomerInquiryService service = new CustomerInquiryService(repository, memberRepository);

    @Test
    @DisplayName("회원정보 없음 테스트")
    void saveCustomerInquiry_shouldThrowCustomException_whenMemberNotFound() {
        // Given
        String memberId = "nonexistentMember";
        CustomerInquiryUserRequestDto requestDto = new CustomerInquiryUserRequestDto(
                "Test Title", "Test Category", "test@example.com", "Test Content"
        );
        Mockito.when(memberRepository.findByMemberId(memberId)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> service.saveCustomerInquiry(requestDto, memberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED.getErrorMessage());
    }

    @Test
    @DisplayName("데이터 저장 실패")
    void saveCustomerInquiry_shouldThrowCustomException_whenSaveFails() {
        //given
        String memberId = "nonexistentMember";
        CustomerInquiryUserRequestDto requestDto = new CustomerInquiryUserRequestDto(
                "Test Title", "Test Category", "test@example.com", "Test Content"
        );
        Member member = new Member();
        member.setMemberId(memberId);
        Mockito.when(memberRepository.findByMemberId(memberId)).thenReturn(member);

        Mockito.when(repository.save(any(CustomerInquiry.class))).thenThrow(new RuntimeException("DB Error"));
        // When & Then
        assertThatThrownBy(() -> service.saveCustomerInquiry(requestDto, memberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.RESOURCE_SAVE_FAILED.getErrorMessage());
    }
}




