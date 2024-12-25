package com.friends.message.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MEMBER_OTHER_EMAIL
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.createTestMessage
import com.friends.message.entity.Message
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class MessageRepositoryTest(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) : DescribeSpec(
        {
            lateinit var member: Member
            lateinit var member2: Member
            lateinit var chatRoom1: ChatRoom
            lateinit var chatRoom2: ChatRoom
            lateinit var chatRoomMember1: ChatRoomMember
            lateinit var chatRoomMember2: ChatRoomMember
            lateinit var message: Message
            beforeEach {
                member = memberRepository.save(createTestMember())
                member2 = memberRepository.save(createTestMember(email = MEMBER_OTHER_EMAIL))
                chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member))
                chatRoom2 = chatRoomRepository.save(createTestChatRoom(manager = member2))
                val enterMessage = messageRepository.save(Message.createEnterMessage(member, chatRoom1))
                messageRepository.save(Message.createEnterMessage(member2, chatRoom1))
                message = messageRepository.save(createTestMessage(chatRoom1, member2))
                chatRoomMember1 = chatRoomMemberRepository.save(createTestChatRoomMember(member = member, chatRoom = chatRoom1, lastReadMessage = enterMessage))
                chatRoomMember2 = chatRoomMemberRepository.save(createTestChatRoomMember(member = member2, chatRoom = chatRoom1, lastReadMessage = message))
            }

            describe("countUnreadMessages 메서드는") {
                context("멤버의 채팅방 연관 정보가 주어졌을 떄 ") {
                    it("읽지 않은 메시지의 개수를 반환한다.") {
                        messageRepository.countUnreadMessages(chatRoomMember1) shouldBe 1 // 입장 메시지 제외하고 1개
                        messageRepository.countUnreadMessages(chatRoomMember2) shouldBe 0 // 모든 메시지를 읽었으므로 0개
                    }
                }
            }
        },
    )
