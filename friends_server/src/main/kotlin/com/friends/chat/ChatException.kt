package com.friends.chat

import com.friends.common.exception.ErrorCode

abstract class ChatException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.errorMessage)

class PositiveLikeCountException : ChatException(ErrorCode.CHAT_ROOM_POSITIVE_LIKE_COUNT)

class ChatRoomNotFoundException : ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND)

class ChatRoomCategoryNotFoundException : ChatException(ErrorCode.CHAT_ROOM_CATEGORY_NOT_FOUND)
