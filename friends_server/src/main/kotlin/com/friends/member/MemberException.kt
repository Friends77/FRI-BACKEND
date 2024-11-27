package com.friends.member

import com.friends.common.exception.ErrorCode

abstract class MemberException(
    val errorCode: ErrorCode,
    val errorMessage: String? = null,
) : RuntimeException(errorMessage ?: errorCode.errorMessage)

class NotFoundMemberException(
    memberId: Long,
) : MemberException(ErrorCode.NOT_FOUND_MEMBER, "$memberId : 존재하지 않는 회원입니다.")
