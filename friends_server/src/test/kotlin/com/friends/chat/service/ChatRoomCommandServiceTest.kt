package com.friends.chat.service

import com.friends.board.repository.CategoryRepository
import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomCreateRequestDto
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.repository.ChatRoomCategoryRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.createTestCategory
import com.friends.member.MEMBER_ID
import com.friends.member.createTestMember
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message
import com.friends.message.repository.MessageRepository
import com.friends.support.createTestImageFile
import io.kotest.assertions.throwables.shouldThrow
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
            val categoryRepository = mockk<CategoryRepository>()
            val chatRoomCategoryRepository = mockk<ChatRoomCategoryRepository>()
            val messageRepository = mockk<MessageRepository>()
            val chatRoomCommandService = ChatRoomCommandService(chatRoomRepository, chatRoomMemberRepository, memberRepository, categoryRepository, chatRoomCategoryRepository, messageRepository)

            given("createChatRoom 테스트") {
                val request = createTestChatRoomCreateRequestDto()
                every { memberRepository.findById(any()) } returns Optional.of(createTestMember())
                every { chatRoomRepository.save(any()) } returns createTestChatRoom()
                every { chatRoomMemberRepository.save(any()) } returns createTestChatRoomMember()
                every { categoryRepository.findByIdIn(any()) } returns listOf(createTestCategory())
                every { chatRoomCategoryRepository.saveAll(any<List<ChatRoomCategory>>()) } returns listOf(ChatRoomCategory.of(createTestChatRoom(), createTestCategory()))
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
                        every { categoryRepository.findByIdIn(any()) } returns emptyList()
                        shouldThrow<ChatRoomCategoryNotFoundException> {
                            chatRoomCommandService.createChatRoom(request, MEMBER_ID, null)
                        }
                    }
                }
            }
        },
    )
