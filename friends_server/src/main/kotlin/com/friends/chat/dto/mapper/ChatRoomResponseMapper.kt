package com.friends.chat.dto.mapper

import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.entity.ChatRoom
import com.friends.common.mapper.toCategoryInfoResponse

fun toChatRoomInfoResponse(
    chatRoom: ChatRoom,
    memberCount: Int,
) = ChatRoomInfoResponseDto(
    id = chatRoom.id,
    title = chatRoom.title,
    imageUrl = chatRoom.imageUrl,
    categoryIdList = chatRoom.categories.map { toCategoryInfoResponse(it.category) },
    participantCount = memberCount,
    likeCount = chatRoom.likeCount,
)
