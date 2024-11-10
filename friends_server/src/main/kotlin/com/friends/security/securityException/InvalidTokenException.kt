package com.friends.security.securityException

import com.friends.common.exception.ErrorCode

open class InvalidJwtException(val errorCode: ErrorCode) :
    RuntimeException(errorCode.errorMessage)

class MissingSocialAccessTokenException() :
    InvalidJwtException(ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN)

class MissingRefreshTokenException() :
    InvalidJwtException(ErrorCode.MISSING_REFRESH_TOKEN)
