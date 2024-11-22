package com.friends.email

import com.friends.common.exception.ErrorCode

abstract class EmailException(
    private val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class EmailSendFailedException : EmailException(ErrorCode.SMTP_CONNECTION_FAILED)
