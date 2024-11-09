package com.friends.security.securityException

import com.friends.common.exception.ErrorCode
import org.springframework.http.HttpStatus

open class InvalidJwtException(val errorCode: ErrorCode, val httpStatus: HttpStatus) :
    RuntimeException(errorCode.errorMessage)

class InvalidRefreshTokenException() :
    InvalidJwtException(ErrorCode.INVALID_REFRESH_TOKEN, ErrorCode.INVALID_REFRESH_TOKEN.httpStatus)

class MissingSocialAccessTokenException() :
    InvalidJwtException(
        ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN,
        ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN.httpStatus
    )

class MissingRefreshTokenException() :
    InvalidJwtException(ErrorCode.MISSING_REFRESH_TOKEN, ErrorCode.MISSING_REFRESH_TOKEN.httpStatus)
