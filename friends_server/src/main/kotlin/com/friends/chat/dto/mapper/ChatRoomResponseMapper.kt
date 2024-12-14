package com.friends.chat.dto.mapper

import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.category.ChatSubjectInfoResponse
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatSubjectCategory

fun toChatRoomInfoResponse(
    chatRoom: ChatRoom,
    memberCount: Int,
) = ChatRoomInfoResponseDto(
    id = chatRoom.id,
    title = chatRoom.title,
    imageUrl = chatRoom.imageUrl,
    categoryIdList = chatRoom.categories.map { toChatSubjectInfoResponse(it.chatSubjectCategory) },
    participantCount = memberCount,
    likeCount = chatRoom.likeCount,
)

fun toChatSubjectInfoResponse(chatSubjectCategory: ChatSubjectCategory) = ChatSubjectInfoResponse(chatSubjectCategory.id, chatSubjectCategory.name)
