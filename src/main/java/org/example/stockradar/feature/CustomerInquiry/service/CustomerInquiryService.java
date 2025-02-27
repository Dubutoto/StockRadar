package org.example.stockradar.feature.CustomerInquiry.service;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.CustomerInquiry.dto.CustomerInquiryUserRequestDto;
import org.example.stockradar.feature.CustomerInquiry.entity.CustomerInquiry;
import org.example.stockradar.feature.CustomerInquiry.repository.CustomerInquiryRepository;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.example.stockradar.global.exception.specific.CustomerInquiryException;
import org.example.stockradar.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class CustomerInquiryService {
    private final CustomerInquiryRepository repository;
    private final MemberRepository memberRepository;

    public Long saveCustomerInquiry(CustomerInquiryUserRequestDto requestDto, String memberId) {
        // 현재 로그인한 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId);

        // 회원이 없는 경우 예외 처리
        if (member == null) {
           throw new CustomException(
                   ErrorCode.UNAUTHORIZED.getErrorCode(),
                   ErrorCode.UNAUTHORIZED.getErrorMessage(),
                   ErrorCode.UNAUTHORIZED.getDescription(),
                   ErrorCode.UNAUTHORIZED.getHttpStatus()
           );
        }
        try {
            CustomerInquiry inquiry = CustomerInquiry.builder()
                    .inquiryTitle(requestDto.getTitle())
                    .inquiryCategory(requestDto.getCategory())
                    .inquiryContent(requestDto.getContent())
                    .inquiryStatus(0)
                    .member(member) // 회원 정보 설정
                    .build();
            return repository.save(inquiry).getInquiryId();
        }
        //데이터 저장 실패
        catch (Exception e) {
            CustomerInquiryException.throwCustomException(ErrorCode.RESOURCE_SAVE_FAILED);

            return null; // 이 코드는 실행되지 않지만 컴파일 에러 방지용
        }


    }
}
