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
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11002, "유효하지 않은 Refresh Token입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11003, "유효하지 않은 Access Token입니다."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, -11002, "RefreshToken 이 존재하지 않습니다."),
    MISSING_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, -11003, "SocialAccessToken 이 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, -11004, "존재하지 않는 이메일입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, -11005, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, -11006, "유효하지 않은 비밀번호입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, -11007, "유효하지 않은 닉네임입니다."),

    // Member API error 12000대
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, -12001, "존재하지 않는 회원입니다."),

    // Email API error 13000대
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, -13001, "유효하지 않은 이메일입니다."),
    INVALID_EMAIL_VERIFY_CODE(HttpStatus.UNAUTHORIZED, -13002, "유효하지 않은 이메일 인증 코드입니다."),
    SMTP_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, -13003, "이메일 서버와의 연결에 실패했습니다."),

    // OAuth2 API error 14000대
    OAUTH2_ACCESS_TOKEN_FETCH_FAILED(HttpStatus.UNAUTHORIZED, -14001, "OAuth2 Access Token 획득에 실패했습니다."),
    OAUTH2_USER_INFO_FETCH_FAILED(HttpStatus.UNAUTHORIZED, -14002, "OAuth2 User 정보 획득에 실패했습니다."),
    OAUTH2_NULL_RESPONSE(HttpStatus.BAD_GATEWAY, -14003, "OAuth2 API로부터 응답이 없습니다."),

    // Profile API error 15000대
    NOT_FOUND_PROFILE(HttpStatus.NOT_FOUND, -15001, "해당 멤버의 프로필이 존재하지 않습니다."),

    // Board API error 16000대
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, -16001, "존재하지 않는 게시물입니다."),
    INVALID_BOARD_ACCESS(HttpStatus.FORBIDDEN, -16002, "게시글에 대한 유효하지 않은 접근입니다."),

    // Chat API error 17000대
    NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, -17001, "존재하지 않는 채팅방입니다."),
    CHAT_ROOM_TITLE_BLANK(HttpStatus.BAD_REQUEST, -17002, "채팅방 제목은 공백일 수 없습니다."),
    CHAT_ROOM_TITLE_INVALID_LENGTH(HttpStatus.BAD_REQUEST, -17003, "채팅방 제목은 1자 이상 20자 이하로 입력해주세요."),
    CHAT_ROOM_CATEGORY_INVALID_SIZE(HttpStatus.BAD_REQUEST, -17004, "채팅방 카테고리는 최소 1개 이상 선택해주세요."),
    CHAT_ROOM_POSITIVE_LIKE_COUNT(HttpStatus.BAD_REQUEST, -17005, "채팅방 좋아요 수는 0 이상이어야 합니다."),
}
