package org.example.stockradar.global.exception.specific;

import org.example.stockradar.global.exception.CustomException;
import org.example.stockradar.global.exception.ErrorCode;

public class AuthException extends CustomException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode.getErrorCode(), errorCode.getErrorMessage(), errorCode.getDescription(), errorCode.getHttpStatus());
    }
}
