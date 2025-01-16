package com.friends.chat

import com.friends.TEST_CATEGORY_ID
import com.friends.TEST_SIZE
import com.friends.category.entity.Category
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomUpdateRequestDto
import com.friends.chat.dto.CreateChatRoomResponseDto
import com.friends.chat.dto.ToggleLikeResponseDto
import com.friends.chat.dto.mapper.toChatRoomDetailResponseDto
import com.friends.chat.dto.mapper.toChatRoomInfoResponse
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.entity.ChatRoomLike
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.createTestCategory
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.message.createMockTestMessage
import com.friends.message.createTestMessage
import com.friends.message.entity.Message
import com.friends.profile.TEST_PROFILE_IMAGE_URL
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

const val TEST_CHAT_ROOM_ID = 1L
const val CHAT_ROOM_TITLE = "테스트 채팅방"
const val CREATE_CHAT_ROOM_REQUEST = "chatRoomCreateRequestDto"
const val CHAT_ROOM_BASE_IMAGE_URL = "chatRoomBaseImageUrl"

fun createTestChatRoom(
    title: String = CHAT_ROOM_TITLE,
    manager: Member = createTestMember(),
    imageUrl: String? = null,
    likeCount: Int = 0,
    categories: List<ChatRoomCategory> = emptyList(),
) = ChatRoom(id = 0L, title = title, manager = manager, imageUrl = imageUrl, likeCount = likeCount, categories = categories)

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
    representativeProfile: List<String> = listOf(TEST_PROFILE_IMAGE_URL),
    unreadMessageCount: Int = 0,
    lastReadMessage: Message = createMockTestMessage(),
    imageUrl: String = CHAT_ROOM_BASE_IMAGE_URL,
) = toChatRoomInfoResponse(chatRoomMember, memberCount, representativeProfile, unreadMessageCount, lastReadMessage, imageUrl)

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
    imageUrl: String = CHAT_ROOM_BASE_IMAGE_URL,
) = toChatRoomDetailResponseDto(chatRoom, memberCount, like, imageUrl)

fun createTestCreateChatRoomResponseDto(
    chatRoomId: Long = TEST_CHAT_ROOM_ID,
) = CreateChatRoomResponseDto(chatRoomId)

fun createTestChatRoomUpdateRequestDto(
    title: String? = null,
    categoryIds: Set<Long> = emptySet(),
    backgroundImageDelete: Boolean = false,
) = ChatRoomUpdateRequestDto(title, categoryIds, backgroundImageDelete)

fun createTestChatRoomCategory(
    chatRoom: ChatRoom = createTestChatRoom(),
    category: Category = createTestCategory(),
) = ChatRoomCategory(0L, chatRoom, category)
