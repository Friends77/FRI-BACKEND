package com.friends.chat.service

import com.friends.category.repository.CategoryRepository
import com.friends.chat.ChatRoomBaseImageCannotDeleteException
import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.ChatRoomMustHaveCategoryException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.ChatRoomUpdateException
import com.friends.chat.NotChatRoomManagerException
import com.friends.chat.TEST_CHAT_ROOM_ID
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomCategory
import com.friends.chat.createTestChatRoomCreateRequestDto
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.createTestChatRoomUpdateRequestDto
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
                every { s3ClientService.upload(any()) } returns "test"
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
            given("updateChatRoom 테스트") {
                val member = createTestMember()
                val chatRoom = createTestChatRoom(categories = listOf(createTestChatRoomCategory()), manager = member)
                every { chatRoomRepository.findById(any()) } returns Optional.of(chatRoom)
                every { memberRepository.findById(any()) } returns Optional.of(member)
                every { chatRoomCategoryRepository.saveAll(any<List<ChatRoomCategory>>()) } returns listOf(ChatRoomCategory.of(chatRoom, createTestCategory()))
                every { chatRoomCategoryRepository.deleteAllInBatch(any()) } returns Unit

                `when`("카테고리가 추가되고 제거될 때") {
                    every { categoryRepository.findByIdIn(any()) } returns listOf(createTestCategory(10L, "test"))
                    val request = createTestChatRoomUpdateRequestDto(categoryIds = setOf(10L))
                    then("카테고리가 추가되고 제거된다.") {
                        chatRoomCommandService.updateChatRoom(TEST_CHAT_ROOM_ID, request, member.id, null)
                        verify(exactly = 1) {
                            chatRoomCategoryRepository.saveAll(any<List<ChatRoomCategory>>())
                            chatRoomCategoryRepository.deleteAllInBatch(any())
                        }
                    }
                }

                `when`("방장이 아닌 경우") {
                    val request = createTestChatRoomUpdateRequestDto()
                    then("NotChatRoomManagerException이 발생한다.") {
                        shouldThrow<NotChatRoomManagerException> {
                            chatRoomCommandService.updateChatRoom(TEST_CHAT_ROOM_ID, request, MEMBER_ID, null)
                        }
                    }
                }

                `when`("기본 이미지를 삭제하려고 할 때") {
                    val request = createTestChatRoomUpdateRequestDto(backgroundImageDelete = true)
                    then("ChatRoomBaseImageCannotDeleteException이 발생한다.") {
                        shouldThrow<ChatRoomBaseImageCannotDeleteException> {
                            chatRoomCommandService.updateChatRoom(TEST_CHAT_ROOM_ID, request, member.id, null)
                        }
                    }
                }

                `when`("전부 존재하지 않는 카테고리 ID가 들어올 경우") {
                    val request = createTestChatRoomUpdateRequestDto(categoryIds = setOf(10L))
                    every { categoryRepository.findByIdIn(any()) } returns emptyList()
                    then("ChatRoomMustHaveCategoryException이 발생한다.") {
                        shouldThrow<ChatRoomMustHaveCategoryException> {
                            chatRoomCommandService.updateChatRoom(TEST_CHAT_ROOM_ID, request, member.id, null)
                        }
                    }
                }

                `when`("카테고리 ID가 empty일 경우") {
                    val request = createTestChatRoomUpdateRequestDto(categoryIds = emptySet())
                    every { categoryRepository.findByIdIn(any()) } returns emptyList()
                    then("ChatRoomMustHaveCategoryException이 발생한다.") {
                        shouldThrow<ChatRoomMustHaveCategoryException> {
                            chatRoomCommandService.updateChatRoom(TEST_CHAT_ROOM_ID, request, member.id, null)
                        }
                    }
                }

                `when`("업데이트할 내용이 없을 때") {
                    then("ChatRoomUpdateException이 발생한다.") {
                        shouldThrow<ChatRoomUpdateException> {
                            chatRoomCommandService.updateChatRoom(TEST_CHAT_ROOM_ID, null, member.id, null)
                        }
                    }
                }
            }
        },
    )
