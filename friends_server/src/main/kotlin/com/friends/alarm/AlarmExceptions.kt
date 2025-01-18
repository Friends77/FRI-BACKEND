package com.friends.alarm

import com.friends.common.exception.ErrorCode

abstract class AlarmExceptions(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class AlarmNotFoundException : AlarmExceptions(ErrorCode.ALARM_NOT_FOUND)
