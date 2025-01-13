package com.friends.chat.service

import com.friends.TEST_SIZE
import com.friends.chat.CHAT_ROOM_BASE_IMAGE_URL
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.TEST_CHAT_ROOM_ID
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestMockSliceChatRoom
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MEMBER_ID
import com.friends.member.createTestMember
import com.friends.member.repository.MemberRepository
import com.friends.message.repository.MessageRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.test.util.ReflectionTestUtils
import java.util.Optional

class ChatRoomQueryServiceTest :
    BehaviorSpec(
        {
            val chatRoomMemberRepository = mockk<ChatRoomMemberRepository>()
            val messageRepository = mockk<MessageRepository>()
            val chatRoomRepository = mockk<ChatRoomRepository>()
            val chatRoomLikeRepository = mockk<ChatRoomLikeRepository>()
            val memberRepository = mockk<MemberRepository>()
            val chatRoomQueryService =
                ChatRoomQueryService(
                    chatRoomMemberRepository,
                    messageRepository,
                    chatRoomRepository,
                    chatRoomLikeRepository,
                    memberRepository,
                ).apply {
                    ReflectionTestUtils.setField(this, "chatRoomBaseImageUrl", CHAT_ROOM_BASE_IMAGE_URL)
                    ReflectionTestUtils.setField(this, "profileBaseImageUrl", CHAT_ROOM_BASE_IMAGE_URL)
                }

            given("getChatRooms 메소드 테스트") {
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                `when`("정상적인 조회 정보가 들어올 경우") {
                    every { chatRoomMemberRepository.countByChatRoom(any()) } returns 10
                    every { chatRoomMemberRepository.sliceChatRoomIdByMember(any(), any(), any(), any()) } returns createTestMockSliceChatRoom()
                    every { messageRepository.countUnreadMessages(any()) } returns 0
                    every { chatRoomMemberRepository.findRepresentativeProfileByChatRoomId(any()) } returns listOf(createTestMember())
                    then("채팅방이 조회된다.") {
                        chatRoomQueryService.getChatRooms(MEMBER_ID, TEST_SIZE, null, null)
                    }
                }

                `when`("해당 회원이 참여하고 있는 채팅방이 없는 경우") {
                    every { chatRoomMemberRepository.sliceChatRoomIdByMember(any(), any(), any(), any()) } returns SliceImpl(listOf(), Pageable.ofSize(TEST_SIZE), false)
                    then("빈 리스트가 반환된다.") {
                        chatRoomQueryService.getChatRooms(MEMBER_ID, TEST_SIZE, null, null)
                        verify(exactly = 0) {
                            chatRoomMemberRepository.countByChatRoom(any())
                            messageRepository.countUnreadMessages(any())
                        }
                    }
                }
            }

            given("getChatRoomDetail 메소드 테스트") {
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                `when`("정상적인 조회 정보가 들어올 경우") {
                    every { chatRoomMemberRepository.countByChatRoom(any()) } returns 10
                    every { chatRoomRepository.findById(any()) } returns Optional.of(createTestChatRoom())
                    every { chatRoomLikeRepository.existsByChatRoomAndMemberId(any(), any()) } returns true
                    then("채팅방이 조회된다.") {
                        chatRoomQueryService.getChatRoomDetail(TEST_CHAT_ROOM_ID, MEMBER_ID)
                    }
                }

                `when`("존재하지 않는 채팅방인 경우") {
                    every { chatRoomRepository.findById(any()) } returns Optional.empty()
                    then("ChatRoomNotFoundException 에러가 발생한다.") {
                        shouldThrow<ChatRoomNotFoundException> { chatRoomQueryService.getChatRoomDetail(TEST_CHAT_ROOM_ID, MEMBER_ID) }
                    }
                }
            }
        },
    )
