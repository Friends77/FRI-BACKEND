package com.friends.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val code: Int, val errorMessage: String) {
    // global error
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, -10000, "Invalid request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -10001, "Internal server error"),

    // Auth API error 11000대
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, -11000, "유효하지 않은 RefreshToken 입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, -11000, "유효하지 않은 토큰입니다."),
    MISSING_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11001, "SocialAccessToken 이 존재하지 않습니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11002, "RefreshToken 이 존재하지 않습니다."),

    // Member API error 12000대
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, -12001, "존재하지 않는 회원입니다."),
}
