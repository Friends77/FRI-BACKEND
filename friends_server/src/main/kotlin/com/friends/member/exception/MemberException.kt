package com.friends.member.exception

import com.friends.common.exception.ErrorCode

open class MemberException(val errorCode: ErrorCode) : RuntimeException(errorCode.errorMessage)

class MemberNotFoundException : MemberException(ErrorCode.NOT_FOUND_MEMBER)

class EmailNotFoundException : MemberException(ErrorCode.NOT_FOUND_EMAIL)
