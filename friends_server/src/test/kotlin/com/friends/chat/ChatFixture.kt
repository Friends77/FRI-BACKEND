package com.friends.chat

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.chat.entity.ChatSubjectCategory
import com.friends.member.createTestMember
import com.friends.member.entity.Member

const val CHAT_ROOM_TITLE = "테스트 채팅방"
const val CHAT_ROOM_CATEGORY_ID = 1L
const val CHAT_ROOM_CATEGORIES = "it"
const val CREATE_CHAT_ROOM_REQUEST = "chatRoomCreateRequestDto"

fun createTestChatRoom(
    title: String = CHAT_ROOM_TITLE,
    manager: Member = createTestMember(),
    imageUrl: String? = null,
) = ChatRoom.of(title, manager, imageUrl)

fun createTestChatRoomCreateRequestDto(
    title: String = CHAT_ROOM_TITLE,
    categories: Set<Long> = setOf(CHAT_ROOM_CATEGORY_ID),
) = ChatRoomCreateRequestDto(title, categories)

fun createTestChatSubjectCategory(
    id: Long = CHAT_ROOM_CATEGORY_ID,
    name: String = CHAT_ROOM_CATEGORIES,
) = ChatSubjectCategory(id, name)

fun createTestChatRoomMember(
    chatRoom: ChatRoom = createTestChatRoom(),
    member: Member = createTestMember(),
) = ChatRoomMember.of(chatRoom, member)
