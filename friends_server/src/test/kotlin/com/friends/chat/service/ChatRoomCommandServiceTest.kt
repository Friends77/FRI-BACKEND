package com.friends.chat.service

import com.friends.chat.CHAT_ROOM_ID
import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomCreateRequestDto
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.createTestChatSubjectCategory
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.entity.Message
import com.friends.chat.repository.ChatRoomCategoryRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.chat.repository.ChatSubjectCategoryRepository
import com.friends.chat.repository.MessageRepository
import com.friends.member.MEMBER_ID
import com.friends.member.createTestMember
import com.friends.member.repository.MemberRepository
import com.friends.support.createTestImageFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ChatRoomCommandServiceTest :
    BehaviorSpec(
        {
            val chatRoomRepository = mockk<ChatRoomRepository>()
            val chatRoomMemberRepository = mockk<ChatRoomMemberRepository>()
            val memberRepository = mockk<MemberRepository>()
            val chatSubjectCategoryRepository = mockk<ChatSubjectCategoryRepository>()
            val chatRoomCategoryRepository = mockk<ChatRoomCategoryRepository>()
            val messageRepository = mockk<MessageRepository>()
            val chatRoomCommandService = ChatRoomCommandService(chatRoomRepository, chatRoomMemberRepository, memberRepository, chatSubjectCategoryRepository, chatRoomCategoryRepository, messageRepository)

            isolationMode = IsolationMode.InstancePerLeaf

            given("createChatRoom 테스트") {
                val request = createTestChatRoomCreateRequestDto()
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                every { chatRoomRepository.save(any()) } returns createTestChatRoom()
                every { chatRoomMemberRepository.save(any()) } returns createTestChatRoomMember()
                every { chatSubjectCategoryRepository.findByIdIn(any()) } returns listOf(createTestChatSubjectCategory())
                every { chatRoomCategoryRepository.saveAll(any<List<ChatRoomCategory>>()) } returns listOf(ChatRoomCategory(createTestChatRoom(), createTestChatSubjectCategory()))
                every { messageRepository.save(any()) } returns Message.createEnterMessage(createTestMember(), createTestChatRoom())
                `when`("정상적인 데이터가 들어올 경우") {
                    then("채팅방이 저장된다.") {
                        chatRoomCommandService.createChatRoom(request, MEMBER_ID, null)
                    }
                }
                `when`("정상적인 데이터와 배경 이미지가 들어 올 경우") {
                    then("채팅방이 정상적으로 생성된다.") {
                        chatRoomCommandService.createChatRoom(
                            request,
                            MEMBER_ID,
                            createTestImageFile(),
                        )
                    }
                }
                `when`("전달 받은 카테고리 ID List에 해당하는 카테고리가 전부 없을 경우") {
                    then("ChatRoomCategoryNotFoundException이 발생한다.") {
                        every { chatSubjectCategoryRepository.findByIdIn(any()) } returns emptyList()
                        shouldThrow<ChatRoomCategoryNotFoundException> {
                            chatRoomCommandService.createChatRoom(request, MEMBER_ID, null)
                        }
                    }
                }
            }

            given("enterChatRoom 테스트") {
                every { chatRoomRepository.findById(any()) } returns Optional.of(createTestChatRoom())
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                every { chatRoomMemberRepository.existsByMemberIdAndChatRoomId(any(), any()) } returns false
                every { chatRoomMemberRepository.save(any()) } returns createTestChatRoomMember()
                every { messageRepository.save(any()) } returns Message.createEnterMessage(createTestMember(), createTestChatRoom())
                `when`("정상적인 데이터가 들어올 경우") {
                    then("채팅방 멤버가 저장된다.") {
                        chatRoomCommandService.enterChatRoom(CHAT_ROOM_ID, MEMBER_ID)
                        verify(exactly = 1) {
                            chatRoomMemberRepository.save(any())
                            messageRepository.save(any())
                        }
                    }
                }

                `when`("이미 채팅방 멤버인 경우") {
                    every { chatRoomMemberRepository.existsByMemberIdAndChatRoomId(any(), any()) } returns true
                    then("채팅방 멤버가 저장되지 않는다.") {
                        chatRoomCommandService.enterChatRoom(CHAT_ROOM_ID, MEMBER_ID)
                        verify(exactly = 1) {
                            chatRoomMemberRepository.existsByMemberIdAndChatRoomId(any(), any())
                        }
                        verify(exactly = 0) {
                            chatRoomMemberRepository.save(any())
                            messageRepository.save(any())
                        }
                    }
                }

                `when`("존재하지 않는 채팅방 ID가 들어올 경우") {
                    then("예외가 발생한다.") {
                        every { chatRoomRepository.findById(any()) } returns Optional.empty()
                        shouldThrow<ChatRoomNotFoundException> {
                            chatRoomCommandService.enterChatRoom(CHAT_ROOM_ID, MEMBER_ID)
                            verify(exactly = 0) { messageRepository.findById(any()) }
                        }
                    }
                }
            }
        },
    )
