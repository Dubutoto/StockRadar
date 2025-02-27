package org.example.stockradar.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 사용자 정의 예외(CustomException) 처리
     * CustomException은 에러 코드, 에러 메시지, 추가 힌트 등을 포함할 수 있도록 설계되어 있습니다.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.error("CustomException 발생: {}", ex.getErrorMessage(), ex);
        // ErrorResponse.of() 메서드는 ErrorCode와 에러 메시지, 추가 정보를 받아 ErrorResponse 객체를 반환하도록 구현합니다.
        ErrorResponse errorResponse = ErrorResponse.of(
                ex.getHttpStatus(),ex.getErrorCode(), ex.getErrorMessage(), ex.getHint()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getHttpStatus()));
    }

    /**
     * 유효성 검증 예외(MethodArgumentNotValidException) 처리
     * 요청 데이터의 유효성 검증에서 발생한 에러 메시지를 추출하여 응답합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error 발생", ex);
        StringBuilder errorMessages = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessages.append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(), // HTTP 상태 코드 추가
                ErrorCode.INVALID_INPUT.getErrorCode(),
                errorMessages.toString(),
                null // 상세 정보는 필요 없으므로 null로 설정
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 그 밖의 모든 예외(Exception) 처리
     * 예상하지 못한 예외가 발생할 경우, 내부 서버 오류로 응답합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unhandled exception 발생", ex);
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // HTTP 상태 코드 추가
                ErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
                "예기치 못한 서버 오류가 발생했습니다.",
                null // 상세 정보는 필요 없으므로 null로 설정
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}