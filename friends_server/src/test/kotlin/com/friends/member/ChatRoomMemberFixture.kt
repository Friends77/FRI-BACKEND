package com.friends.member

import com.friends.chat.CHAT_ROOM_ID
import com.friends.member.entity.ChatRoomMember
import com.friends.member.entity.Member

fun createTestChatRoomMember(
    chatRoomId: String = CHAT_ROOM_ID,
    member: Member = createTestMember(),
) = ChatRoomMember.of(chatRoomId, member)
