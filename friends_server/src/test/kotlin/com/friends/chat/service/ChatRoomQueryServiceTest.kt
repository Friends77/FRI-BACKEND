package com.friends.chat.service

import com.friends.TEST_SIZE
import com.friends.chat.createTestMockSliceChatRoom
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.member.MEMBER_ID
import com.friends.message.repository.MessageRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl

class ChatRoomQueryServiceTest :
    BehaviorSpec(
        {
            val chatRoomMemberRepository = mockk<ChatRoomMemberRepository>()
            val messageRepository = mockk<MessageRepository>()
            val chatRoomQueryService = ChatRoomQueryService(chatRoomMemberRepository, messageRepository)

            given("getChatRooms 메소드 테스트") {
                `when`("정상적인 조회 정보가 들어올 경우") {
                    every { chatRoomMemberRepository.countByChatRoom(any()) } returns 10
                    every { chatRoomMemberRepository.sliceChatRoomIdByMember(any(), any(), any(), any()) } returns createTestMockSliceChatRoom()
                    every { messageRepository.countUnreadMessages(any()) } returns 0
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
        },
    )
