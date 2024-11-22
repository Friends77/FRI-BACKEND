package com.friends.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val code: Int,
    val errorMessage: String,
) {
    // global error
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, -10000, "적절하지 않은 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -10001, "서버 내부 오류입니다."),

    // Auth API error 11000대
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, -11001, "유효하지 않은 토큰입니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11002, "RefreshToken 이 존재하지 않습니다."),
    MISSING_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11003, "SocialAccessToken 이 존재하지 않습니다."),

    // Member API error 12000대
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, -12001, "존재하지 않는 회원입니다."),

    // Email API error 13000대
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, -13001, "유효하지 않은 이메일입니다."),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.UNAUTHORIZED, -13002, "유효하지 않은 이메일 인증 코드입니다."),
    SMTP_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, -13003, "이메일 서버와의 연결에 실패했습니다."),
}
