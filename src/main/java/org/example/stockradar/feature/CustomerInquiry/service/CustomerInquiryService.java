package org.example.stockradar.feature.CustomerInquiry.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryUserRequestDto;
import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.CustomerInquiry.repository.CustomerInquiryRepository;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerInquiryService {
    private final CustomerInquiryRepository repository;
    private final MemberRepository memberRepository;

    public Long saveCustomerInquiry(CustomerInquiryUserRequestDto requestDto, String memberId) {
        // 현재 로그인한 회원 정보 조회 - Optional 사용하지 않음
        Member member = memberRepository.findByMemberId(memberId);

        // 회원이 없는 경우 예외 처리
        if (member == null) {
            throw new RuntimeException("회원을 찾을 수 없습니다: " + memberId);
        }

        CustomerInquiry inquiry = CustomerInquiry.builder()
                .inquiryTitle(requestDto.getTitle())
                .inquiryCategory(requestDto.getCategory())
                .inquiryContent(requestDto.getContent())
                .inquiryStatus("접수완료")
                .member(member) // 회원 정보 설정
                .build();

        return repository.save(inquiry).getInquiryId();
    }
}
