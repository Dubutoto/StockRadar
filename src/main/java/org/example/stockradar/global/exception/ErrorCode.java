package org.example.stockradar.global.exception;

import lombok.Getter;
import lombok.AllArgsConstructor;

// ErrorCode.java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_INPUT(400, "ERR-001", "잘못된 입력", "입력 값이 유효하지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(401, "ERR-002", "인증 실패", "인증 정보가 없거나 유효하지 않습니다."),
    TOKEN_EXPIRED(401, "ERR-006", "토큰 만료", "인증 토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "ERR-009", "유효하지 않은 토큰", "토큰이 손상되었거나 잘못된 형식입니다."),
    LOGIN_FAILED(401, "ERR-010", "로그인 실패", "아이디 또는 비밀번호가 잘못되었습니다."),
    ACCOUNT_LOCKED(401, "ERR-011", "계정 잠김", "계정이 잠겼습니다. 관리자에게 문의하세요."),
    ACCOUNT_DISABLED(401, "ERR-012", "계정 비활성화", "비활성화된 계정입니다. 관리자에게 문의하세요."),

    // 403 Forbidden
    FORBIDDEN(403, "ERR-003", "접근 거부", "해당 리소스에 대한 접근 권한이 없습니다."),
    INSUFFICIENT_PERMISSIONS(403, "ERR-013", "권한 부족", "요청을 수행할 권한이 없습니다."),

    // 404 Not Found
    RESOURCE_NOT_FOUND(404, "ERR-004", "리소스 없음", "요청하신 리소스를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(404, "ERR-007", "회원 없음", "해당 회원을 찾을 수 없습니다."),
    INQUIRY_NOT_FOUND(404, "ERR-014", "문의 없음", "고객문의가 존재하지 않습니다."),
    STOCK_INFO_NOT_FOUND(404, "ERR-203", "재고 정보 없음", "상품의 재고 정보를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "ERR-004", "상품 없음", "상품을 조회 할수 없습니다."),
    PRODUCT_ID_NULL(400, "ERR-204", "상품 ID 없음", "상품 ID가 null입니다. 데이터 무결성 오류가 발생했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "ERR-005", "서버 오류", "관리자에게 문의하세요."),
    RESOURCE_SAVE_FAILED(500, "ERR-009", "데이터 저장 실패", "데이터베이스에 데이터를 저장하는 중 오류가 발생했습니다."),
    DATA_CONVERSION_ERROR(500, "ERR-015", "데이터 변환 오류", "데이터 DTO변환중 오류가 발생했습니다."),
    SCHEDULER_EXECUTION_FAILED(500, "ERR-101", "스케줄러 실행 실패", "스케줄러 작업 실행 중 오류가 발생했습니다."),
    DATA_FETCH_TIMEOUT(500, "ERR-102", "데이터 조회 시간 초과", "데이터 조회 중 시간 초과가 발생했습니다."),
    CONCURRENT_SCHEDULER_CONFLICT(500, "ERR-103", "스케줄러 충돌", "이전 스케줄러 작업이 아직 실행 중입니다.");


    private final int httpStatus;          // HTTP 상태 코드
    private final String errorCode;        // 고유 에러 코드
    private final String errorMessage;     // 에러 메시지
    private final String description;      // 상세 설명
}
