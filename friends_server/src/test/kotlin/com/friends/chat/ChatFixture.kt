package com.friends.chat

import com.friends.TEST_CATEGORY_ID
import com.friends.TEST_SIZE
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.mapper.toChatRoomInfoResponse
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

const val CHAT_ROOM_TITLE = "테스트 채팅방"
const val CREATE_CHAT_ROOM_REQUEST = "chatRoomCreateRequestDto"

fun createTestChatRoom(
    title: String = CHAT_ROOM_TITLE,
    manager: Member = createTestMember(),
    imageUrl: String? = null,
) = ChatRoom.of(title, manager, imageUrl)

fun createTestChatRoomCreateRequestDto(
    title: String = CHAT_ROOM_TITLE,
    categories: Set<Long> = setOf(TEST_CATEGORY_ID),
) = ChatRoomCreateRequestDto(title, categories)

fun createTestChatRoomMember(
    chatRoom: ChatRoom = createTestChatRoom(),
    member: Member = createTestMember(),
) = ChatRoomMember.of(chatRoom, member)

fun createTestSliceChatRoom(
    chatRoom: ChatRoom = createTestChatRoom(),
) = SliceImpl(listOf(chatRoom), Pageable.ofSize(TEST_SIZE), false)

fun createTestChatRoomInfoResponseDto(
    chatRoom: ChatRoom = createTestChatRoom(),
    memberCount: Int = TEST_SIZE,
) = toChatRoomInfoResponse(chatRoom, memberCount)

fun createTestSliceResponseChatRoom(
    sliceChatRoom: Slice<ChatRoom> = createTestSliceChatRoom(),
) = toSliceBaseResponse(sliceChatRoom.map { createTestChatRoomInfoResponseDto(it) })
