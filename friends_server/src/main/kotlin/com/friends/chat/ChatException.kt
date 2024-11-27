package com.friends.chat

import com.friends.common.exception.ErrorCode

abstract class ChatException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class ChatRoomNotFoundException : ChatException(ErrorCode.NOT_FOUND_CHAT_ROOM)
