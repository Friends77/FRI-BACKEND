package com.friends.chat.service

import com.friends.TEST_SIZE
import com.friends.chat.createTestSliceChatRoom
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.member.MEMBER_ID
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

class ChatRoomQueryServiceTest :
    BehaviorSpec(
        {
            val chatRoomMemberRepository = mockk<ChatRoomMemberRepository>()
            val chatRoomQueryService = ChatRoomQueryService(chatRoomMemberRepository)

            given("getChatRooms 메소드 테스트") {
                every { chatRoomMemberRepository.countByChatRoom(any()) } returns 10
                every { chatRoomMemberRepository.sliceChatRoomIdByMember(any(), any(), any(), any()) } returns createTestSliceChatRoom()
                `when`("정상적인 조회 정보가 들어올 경우") {
                    then("채팅방이 조회된다.") {
                        chatRoomQueryService.getChatRooms(MEMBER_ID, TEST_SIZE, null, null)
                    }
                }
            }
        },
    )
