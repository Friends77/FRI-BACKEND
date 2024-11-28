package com.friends.profile

import com.friends.common.exception.ErrorCode

abstract class ProfileExceptions (
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class ProfileNullResponseException : ProfileExceptions(ErrorCode.NOT_FOUND_PROFILE)