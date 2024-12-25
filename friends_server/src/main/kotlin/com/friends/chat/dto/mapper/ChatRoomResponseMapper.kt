package com.friends.chat.dto.mapper

import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.mapper.toCategoryInfoResponse

fun toChatRoomInfoResponse(
    chatRoomMember: ChatRoomMember,
    memberCount: Int,
    unreadMessageCount: Int,
): ChatRoomInfoResponseDto {
    val lastChatRoomMessageTime =
        // 마지막 읽은 메세지가 채팅방의 마지막 메세지보다 이전이면 채팅방의 마지막 메세지 시간을 가져옴 그렇지 않으면 자신의 입장 메세지가 보내진 시간을 갖고옴(마지막 읽은 메세지 자신의 입장 메세지로 초기화하기 때문)
        if (chatRoomMember.chatRoom.lastMessage != null &&
            chatRoomMember.chatRoom.lastMessage!!
                .createdAt
                .isAfter(chatRoomMember.lastReadMessage.createdAt)
        ) {
            chatRoomMember.chatRoom.lastMessage!!.createdAt
        } else {
            chatRoomMember.lastReadMessage.createdAt
        }
    return ChatRoomInfoResponseDto(
        chatRoomMemberId = chatRoomMember.id,
        id = chatRoomMember.chatRoom.id,
        title = chatRoomMember.chatRoom.title,
        imageUrl = chatRoomMember.chatRoom.imageUrl,
        categoryIdList = chatRoomMember.chatRoom.categories.map { toCategoryInfoResponse(it.category) },
        participantCount = memberCount,
        lastMessageTime = lastChatRoomMessageTime,
        unreadMessageCount = unreadMessageCount,
    )
}
