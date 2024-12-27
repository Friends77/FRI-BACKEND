package com.friends.message.service

import com.friends.board.createTestMember
import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.createTestMessage
import com.friends.message.entity.Message
import com.friends.message.repository.MessageRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class MessageQueryServiceTest(
    private val messageQueryService: MessageQueryService,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val messageRepository: MessageRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) : DescribeSpec(
        {
            lateinit var member: Member
            lateinit var chatRoom: ChatRoom
            lateinit var chatRoomMember: ChatRoomMember
            lateinit var lastReadMessage: Message
            beforeEach {
                member = memberRepository.save(createTestMember())
                chatRoom = chatRoomRepository.save(createTestChatRoom(manager = member))
                val enterMessage = messageRepository.save(Message.createEnterMessage(member, chatRoom))
                chatRoomMember = chatRoomMemberRepository.save(createTestChatRoomMember(member = member, chatRoom = chatRoom, lastReadMessage = enterMessage))

                for (i in 1..10) {
                    val message = messageRepository.save(createTestMessage(chatRoom, member))
                    if (i == 5) {
                        lastReadMessage = message
                    }
                }
            }

            describe("getUnreadMessage 메서드는") {
                context("멤버의 채팅방 연관 정보가 주어졌을 때") {
                    it("읽지 않은 메시지를 반환한다.") {
                        val unreadMessages = messageQueryService.getUnreadMessage(member.id, chatRoom.id)
                        unreadMessages.content.size shouldBe 5
                    }
                }
            }

            describe("getPreviousMessages 메서드는") {
                context("채팅방의 메시지 id와 사이즈가 주어졌을 때") {
                    it("이전 메시지를 반환한다.") {
                        val previousMessages = messageQueryService.getPreviousMessages(chatRoom.id, lastReadMessage.id, 5)
                        previousMessages.content.size shouldBe 4
                        previousMessages.hasNext shouldBe false
                    }
                }
            }
        },
    )
