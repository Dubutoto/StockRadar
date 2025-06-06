package org.example.stockradar.global.exception.specific;

import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;

public class CustomerInquiryException {

    // 특정 에러 코드를 기반으로 CustomException을 던지는 메서드
    public static void throwCustomException(ErrorCode errorCode) {
        throw new CustomException(
                errorCode.getErrorCode(),
                errorCode.getErrorMessage(),
                errorCode.getDescription(),
                errorCode.getHttpStatus()
        );
    }

    // 에러 메시지를 추가로 제공하여 CustomException을 던지는 메서드
    public static void throwCustomException(ErrorCode errorCode, String customMessage) {
        throw new CustomException(
                errorCode.getErrorCode(),
                customMessage,
                errorCode.getDescription(),
                errorCode.getHttpStatus()
        );
    }
}
