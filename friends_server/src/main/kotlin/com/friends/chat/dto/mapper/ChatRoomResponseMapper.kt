package com.friends.chat.dto.mapper

import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.mapper.toCategoryInfoResponse
import com.friends.message.entity.Message

fun toChatRoomInfoResponse(
    chatRoomMember: ChatRoomMember,
    memberCount: Int,
    representativeProfile: List<String>,
    unreadMessageCount: Int,
    lastMessage: Message?,
    imageUrl: String,
): ChatRoomInfoResponseDto =
    ChatRoomInfoResponseDto(
        chatRoomMemberId = chatRoomMember.id,
        id = chatRoomMember.chatRoom.id,
        title = chatRoomMember.chatRoom.title,
        imageUrl = imageUrl,
        categoryIdList = chatRoomMember.chatRoom.categories.map { toCategoryInfoResponse(it.category) },
        participantCount = memberCount,
        participantProfileList = representativeProfile,
        lastMessageTime = lastMessage?.createdAt,
        lastMessage = lastMessage?.content,
        unreadMessageCount = unreadMessageCount,
    )

fun toChatRoomDetailResponseDto(
    chatRoom: ChatRoom,
    memberCount: Int,
    isLike: Boolean,
    imageUrl: String,
) = ChatRoomDetailResponseDto(
    id = chatRoom.id,
    title = chatRoom.title,
    imageUrl = imageUrl,
    categoryIdList = chatRoom.categories.map { toCategoryInfoResponse(it.category) },
    participantCount = memberCount,
    likeCount = chatRoom.likeCount,
    isLike = isLike,
)
