package com.friends.chat.dto.mapper

import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.mapper.toCategoryInfoResponse

fun toChatRoomInfoResponse(
    chatRoomMember: ChatRoomMember,
    memberCount: Int,
    unreadMessageCount: Int,
) = ChatRoomInfoResponseDto(
    id = chatRoomMember.chatRoom.id,
    title = chatRoomMember.chatRoom.title,
    imageUrl = chatRoomMember.chatRoom.imageUrl,
    categoryIdList = chatRoomMember.chatRoom.categories.map { toCategoryInfoResponse(it.category) },
    participantCount = memberCount,
    lastMessageTime = chatRoomMember.chatRoom.lastMessage?.createdAt ?: chatRoomMember.lastReadMessage.createdAt, // 회원이 들어오고나서 다른 사람이 채팅을 했으면 채팅방의 마지막 메세지 시간을 갖고오지만 그렇지 않으면 자신의 입장 메세지가 보내진 시간을 갖고옴(마지막 읽은 메세지 자신의 입장 메세지로 초기화하기 때문)
    unreadMessageCount = unreadMessageCount,
)
