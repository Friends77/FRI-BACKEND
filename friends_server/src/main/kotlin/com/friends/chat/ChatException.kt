package com.friends.chat

import com.friends.common.exception.ErrorCode

abstract class ChatException(
    val errorCode: ErrorCode,
    cause: Throwable? = null,
) : RuntimeException(errorCode.errorMessage, cause)

class PositiveLikeCountException : ChatException(ErrorCode.CHAT_ROOM_POSITIVE_LIKE_COUNT)

class ChatRoomNotFoundException : ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND)

class ChatRoomMemberNotFoundException : ChatException(ErrorCode.CHAT_ROOM_MEMBER_NOT_FOUND)

class ChatRoomCategoryNotFoundException : ChatException(ErrorCode.CHAT_ROOM_CATEGORY_NOT_FOUND)

class UnexpectedChatRoomException(
    cause: Throwable,
) : ChatException(ErrorCode.UNEXPECTED_CHAT_ROOM, cause)

class NotChatRoomMemberException : ChatException(ErrorCode.NOT_A_MEMBER_OF_CHAT_ROOM)
