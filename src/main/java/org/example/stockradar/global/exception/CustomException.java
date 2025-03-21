package org.example.stockradar.global.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class CustomException extends RuntimeException {

    // 예외를 식별할 수 있는 에러 코드
    private final String errorCode;

    // 사용자에게 전달할 에러 메시지
    private final String errorMessage;

    // 문제 해결에 도움이 될 추가 정보나 힌트 (옵션)
    private final String hint;

    private final int httpStatus;


}
