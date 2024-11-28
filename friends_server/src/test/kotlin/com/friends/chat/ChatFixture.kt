package com.friends.chat

import com.friends.chat.dto.ChatRoomRequest
import com.friends.chat.entity.ChatRoom
import com.friends.member.MEMBER_ID

const val CHAT_ROOM_ID = "test"
const val CHAT_ROOM_TITLE = "테스트 채팅방"
const val CHAT_ROOM_CATEGORIES = "it"
const val CREATE_CHAT_ROOM_REQUEST = "chatRoomRequest"

fun createTestChatRoom(
    id: String? = null,
    title: String = CHAT_ROOM_TITLE,
    createrId: Long = MEMBER_ID,
    categories: MutableList<String> = mutableListOf(CHAT_ROOM_CATEGORIES),
    imageUrl: String? = null,
) = ChatRoom.of(title, createrId, categories, imageUrl, id)

fun createTestChatRoomRequest(
    title: String = CHAT_ROOM_TITLE,
    categories: MutableList<String> = mutableListOf(CHAT_ROOM_CATEGORIES),
) = ChatRoomRequest(title, categories)
