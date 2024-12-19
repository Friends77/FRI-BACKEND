package com.friends.chat.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomLike
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MEMBER_ID
import com.friends.member.createTestMember
import com.friends.member.repository.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ChatRoomLikeCommandServiceTest :
    BehaviorSpec(
        {
            val chatRoomRepository = mockk<ChatRoomRepository>()
            val memberRepository = mockk<MemberRepository>()
            val chatRoomLikeRepository = mockk<ChatRoomLikeRepository>()
            val chatRoomLikeCommandService = ChatRoomLikeCommandService(chatRoomRepository, chatRoomLikeRepository, memberRepository)

            given("toggleLike 테스트") {
                val chatRoom = createTestChatRoom()
                val member = createTestMember()
                every { chatRoomRepository.findById(any()) } returns Optional.of(chatRoom)
                every { memberRepository.findById(any()) } returns Optional.of(member)
                every { chatRoomLikeRepository.existsByChatRoomAndMember(any(), any()) } returns false
                every { chatRoomLikeRepository.save(any()) } returns createTestChatRoomLike()
                every { chatRoomLikeRepository.countByChatRoom(any()) } returns 1
                `when`("좋아요를 누르지 않은 상태에서 좋아요를 누를 경우") {
                    then("좋아요가 저장된다.") {
                        chatRoomLikeCommandService.toggleLike(MEMBER_ID, chatRoom.id)
                        verify(exactly = 1) {
                            chatRoomLikeRepository.save(any())
                        }
                    }
                }

                every { chatRoomLikeRepository.existsByChatRoomAndMember(any(), any()) } returns true
                every { chatRoomLikeRepository.deleteByChatRoomAndMember(any(), any()) } returns Unit
                every { chatRoomLikeRepository.countByChatRoom(any()) } returns 0
                `when`("좋아요를 누른 상태에서 좋아요를 취소할 경우") {
                    then("좋아요가 삭제된다.") {
                        chatRoomLikeCommandService.toggleLike(MEMBER_ID, chatRoom.id)
                        verify(exactly = 0) {
                            chatRoomLikeRepository.save(any())
                        }
                    }
                }

                `when`("존재하지 않는 채팅방 ID를 전달할 경우") {
                    then("ChatRoomNotFoundException 발생한다.") {
                        every { chatRoomRepository.findById(any()) } returns Optional.empty()
                        shouldThrow<ChatRoomNotFoundException> { chatRoomLikeCommandService.toggleLike(MEMBER_ID, chatRoom.id) }
                    }
                }
            }
        },
    )
