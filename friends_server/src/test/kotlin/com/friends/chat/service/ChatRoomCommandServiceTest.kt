package com.friends.chat.service

import com.friends.category.repository.CategoryRepository
import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.TEST_CHAT_ROOM_ID
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomCreateRequestDto
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.repository.ChatRoomCategoryRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.createTestCategory
import com.friends.image.S3ClientService
import com.friends.member.MEMBER_ID
import com.friends.member.createTestMember
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message
import com.friends.message.service.MessageCommandService
import com.friends.support.createTestImageFile
import io.kotest.assertions.throwables.shouldThrow
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
            val categoryRepository = mockk<CategoryRepository>()
            val chatRoomCategoryRepository = mockk<ChatRoomCategoryRepository>()
            val s3ClientService = mockk<S3ClientService>()
            val messageCommandService = mockk<MessageCommandService>()
            val chatRoomCommandService = ChatRoomCommandService(chatRoomRepository, chatRoomMemberRepository, memberRepository, categoryRepository, chatRoomCategoryRepository, s3ClientService, messageCommandService)

            given("createChatRoom 테스트") {
                val request = createTestChatRoomCreateRequestDto()
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                every { chatRoomRepository.save(any()) } returns createTestChatRoom()
                every { chatRoomMemberRepository.save(any()) } returns createTestChatRoomMember()
                every { categoryRepository.findByIdIn(any()) } returns listOf(createTestCategory())
                every { chatRoomCategoryRepository.saveAll(any<List<ChatRoomCategory>>()) } returns listOf(ChatRoomCategory.of(createTestChatRoom(), createTestCategory()))
                every { messageCommandService.sendMessage(any(), any(), any(), any()) } returns Message.createEnterMessage(createTestMember(), createTestChatRoom())
                every { messageCommandService.setChatRoomOnline(any(), any()) } returns Unit
                every { s3ClientService.upload(any(), any()) } returns "test"
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
                        every { categoryRepository.findByIdIn(any()) } returns emptyList()
                        shouldThrow<ChatRoomCategoryNotFoundException> {
                            chatRoomCommandService.createChatRoom(request, MEMBER_ID, null)
                        }
                    }
                }
            }

            given("enterChatRoom 테스트") {
                every { chatRoomRepository.findById(any()) } returns Optional.of(createTestChatRoom())
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                every { chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(any(), any()) } returns false
                every { chatRoomMemberRepository.save(any()) } returns createTestChatRoomMember()
                every { messageCommandService.sendMessage(any(), any(), any(), any()) } returns Message.createEnterMessage(createTestMember(), createTestChatRoom())
                every { messageCommandService.setChatRoomOnline(any(), any()) } returns Unit
                `when`("정상적인 데이터가 들어올 경우") {
                    then("채팅방 멤버가 저장된다.") {
                        chatRoomCommandService.enterChatRoom(TEST_CHAT_ROOM_ID, MEMBER_ID)
                        verify(exactly = 1) {
                            chatRoomMemberRepository.save(any())
                            messageCommandService.sendMessage(any(), any(), any(), any())
                        }
                    }
                }

                `when`("이미 채팅방 멤버인 경우") {
                    every { chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(any(), any()) } returns true
                    then("채팅방 멤버가 저장되지 않는다.") {
                        chatRoomCommandService.enterChatRoom(TEST_CHAT_ROOM_ID, MEMBER_ID)
                        verify(exactly = 1) {
                            chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(any(), any())
                        }
                        verify(exactly = 0) {
                            chatRoomMemberRepository.save(any())
                            messageCommandService.sendMessage(any(), any(), any(), any())
                        }
                    }
                }

                `when`("존재하지 않는 채팅방 ID가 들어올 경우") {
                    then("예외가 발생한다.") {
                        every { chatRoomRepository.findById(any()) } returns Optional.empty()
                        shouldThrow<ChatRoomNotFoundException> {
                            chatRoomCommandService.enterChatRoom(TEST_CHAT_ROOM_ID, MEMBER_ID)
                            verify(exactly = 0) { messageCommandService.sendMessage(any(), any(), any(), any()) }
                        }
                    }
                }
            }
        },
    )
