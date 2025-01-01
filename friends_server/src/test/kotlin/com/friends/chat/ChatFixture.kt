package com.friends.chat

import com.friends.TEST_CATEGORY_ID
import com.friends.TEST_SIZE
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.CreateChatRoomResponseDto
import com.friends.chat.dto.ToggleLikeResponseDto
import com.friends.chat.dto.mapper.toChatRoomDetailResponseDto
import com.friends.chat.dto.mapper.toChatRoomInfoResponse
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomLike
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.message.createMockTestMessage
import com.friends.message.createTestMessage
import com.friends.message.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

const val TEST_CHAT_ROOM_ID = 1L
const val CHAT_ROOM_TITLE = "테스트 채팅방"
const val CREATE_CHAT_ROOM_REQUEST = "chatRoomCreateRequestDto"

fun createTestChatRoom(
    title: String = CHAT_ROOM_TITLE,
    manager: Member = createTestMember(),
    imageUrl: String? = null,
    likeCount: Int = 0,
) = ChatRoom(id = 0L, title = title, manager = manager, imageUrl = imageUrl, likeCount = likeCount)

fun createTestChatRoomCreateRequestDto(
    title: String = CHAT_ROOM_TITLE,
    categories: Set<Long> = setOf(TEST_CATEGORY_ID),
) = ChatRoomCreateRequestDto(title, categories)

fun createTestChatRoomMember(
    chatRoom: ChatRoom = createTestChatRoom(),
    member: Member = createTestMember(),
    lastReadMessage: Message = createTestMessage(),
) = ChatRoomMember.of(
    chatRoom,
    member,
    lastReadMessage,
)

fun createTestSliceChatRoom(
    chatRoomMember: ChatRoomMember = createTestChatRoomMember(),
) = SliceImpl(listOf(chatRoomMember), Pageable.ofSize(TEST_SIZE), false)

fun createTestMockSliceChatRoom(
    chatRoomMember: ChatRoomMember = ChatRoomMember.of(createTestChatRoom(), createTestMember(), createMockTestMessage()),
) = SliceImpl(listOf(chatRoomMember), Pageable.ofSize(TEST_SIZE), false)

fun createTestChatRoomInfoResponseDto(
    chatRoomMember: ChatRoomMember = createTestChatRoomMember(),
    memberCount: Int = TEST_SIZE,
    unreadMessageCount: Int = 0,
) = toChatRoomInfoResponse(chatRoomMember, memberCount, unreadMessageCount)

fun createTestSliceResponseChatRoom(
    sliceChatRoom: Slice<ChatRoomMember> = createTestSliceChatRoom(),
) = toSliceBaseResponse(sliceChatRoom.map { createTestChatRoomInfoResponseDto(it) })

fun createTestMockSliceResponseChatRoom(
    sliceChatRoom: Slice<ChatRoomMember> = createTestMockSliceChatRoom(),
) = toSliceBaseResponse(sliceChatRoom.map { createTestChatRoomInfoResponseDto(it) })

fun createTestToggleLikeResponseDto(
    chatRoomId: Long = TEST_CHAT_ROOM_ID,
    like: Boolean = true,
    likeCount: Int = TEST_SIZE,
) = ToggleLikeResponseDto(chatRoomId, likeCount, like)

fun createTestChatRoomLike(
    chatRoom: ChatRoom = createTestChatRoom(),
    member: Member = createTestMember(),
) = ChatRoomLike.of(chatRoom, member)

fun createTestChatRoomDetailResponseDto(
    chatRoom: ChatRoom = createTestChatRoom(),
    memberCount: Int = TEST_SIZE,
    like: Boolean = false,
) = toChatRoomDetailResponseDto(chatRoom, memberCount, like)

fun createTestCreateChatRoomResponseDto(
    chatRoomId: Long = TEST_CHAT_ROOM_ID,
) = CreateChatRoomResponseDto(chatRoomId)
