package org.example.stockradar.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ErrorResponse {

    // 오류가 발생한 시각
    private LocalDateTime timestamp;

    // HTTP 상태 코드 (예: 404, 500 등)
    private int httpStatus;

    // 고유 에러 코드 (예: ERR-001)
    private String errorCode;

    // 에러 메시지 (사용자에게 보여줄 메시지)
    private String errorMessage;

    // 추가적인 상세 정보 (예: 해결 방법에 대한 힌트)
    private String details;

    /**
     * 정적 팩토리 메소드: 현재 시간을 타임스탬프로 자동 설정하여 ErrorResponse 객체를 생성합니다.
     *
     * @param httpStatus   HTTP 상태 코드
     * @param errorCode    고유 에러 코드
     * @param errorMessage 에러 메시지
     * @param details      추가 상세 정보 또는 힌트
     * @return 생성된 ErrorResponse 객체
     */
    public static ErrorResponse of(int httpStatus, String errorCode, String errorMessage, String details) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(0) // 기본값 또는 적절한 값을 설정
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .details(null) // 상세 정보가 없을 경우 null로 설정
                .build();
    }



}