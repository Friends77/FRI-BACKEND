package com.friends.common.exception

open class ErrorResponse private constructor(val code: Int, val errorMessage: String) {
    companion object {
        fun of(
            errorCode: ErrorCode,
            errorMessage: String?,
        ) = ErrorResponse(errorCode.code, errorMessage ?: errorCode.errorMessage)
    }
}
