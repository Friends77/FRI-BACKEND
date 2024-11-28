package com.friends.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val code: Int, val errorMessage: String) {
    // global error
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, -10000, "적절하지 않은 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -10001, "서버 내부 오류입니다."),

    // Auth API error 11000대
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, -11001, "유효하지 않은 토큰입니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11002, "RefreshToken 이 존재하지 않습니다."),
    MISSING_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11003, "SocialAccessToken 이 존재하지 않습니다."),

    // Member API error 12000대
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, -12001, "존재하지 않는 회원입니다."),

}
