package com.friends.chat.service

import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomRequest
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MEMBER_ID
import com.friends.member.createTestChatRoomMember
import com.friends.member.createTestMember
import com.friends.member.repository.ChatRoomMemberRepository
import com.friends.member.repository.MemberRepository
import com.friends.support.createTestImageFile
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class ChatRoomCommandServiceTest :
    BehaviorSpec(
        {
            val chatRoomRepository = mockk<ChatRoomRepository>()
            val chatRoomMemberRepository = mockk<ChatRoomMemberRepository>()
            val memberRepository = mockk<MemberRepository>()
            val chatRoomCommandService = ChatRoomCommandService(chatRoomRepository, chatRoomMemberRepository, memberRepository)
            given("createChatRoom 테스트") {
                val requst = createTestChatRoomRequest()
                every { chatRoomRepository.save(any()) } returns createTestChatRoom()
                every { chatRoomMemberRepository.save(any()) } returns createTestChatRoomMember()
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                `when`("정상적인 데이터가 들어올 경우") {
                    then("채팅방이 저장된다.") {
                        chatRoomCommandService.createChatRoom(requst, MEMBER_ID, null)
                    }
                }
                `when`("정상적인 데이터와 배경 이미지가 들어 올 경우") {
                    then("채팅방이 정상적으로 생성된다.") {
                        chatRoomCommandService.createChatRoom(
                            requst,
                            MEMBER_ID,
                            createTestImageFile(),
                        )
                    }
                }
            }
        },
    )
