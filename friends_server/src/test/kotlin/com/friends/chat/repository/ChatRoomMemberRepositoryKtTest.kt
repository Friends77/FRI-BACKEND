package com.friends.chat.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message.Companion.createEnterMessage
import com.friends.message.repository.MessageRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class ChatRoomMemberRepositoryKtTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messageRepository: MessageRepository,
) : DescribeSpec(
        {
            lateinit var member: Member
            lateinit var chatRoom1: ChatRoom
            lateinit var chatRoomMember1: ChatRoomMember
            beforeEach {
                member = memberRepository.save(createTestMember())
                chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member))
                val enterMessage = messageRepository.save(createEnterMessage(member, chatRoom1))
                chatRoomMember1 = chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom1, member, enterMessage))
            }
            afterEach {
                chatRoomMemberRepository.delete(chatRoomMember1)
                chatRoomRepository.delete(chatRoom1)
                memberRepository.delete(member)
            }

            describe("findByChatRoomAndMember") {
                context("참여중인 채팅방을 조회하면") {
                    it("해당 채팅방을 반환한다") {
                        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom1, member)
                        chatRoomMember!!.id shouldBe chatRoomMember1.id
                    }
                }
            }

            describe("save") {
                context("이미 존재하는 ChatRoomId와 MemberId가 들어오는 경우") {
                    it("에러가 난다.") {
                        shouldThrow<Exception> {
                            chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom1, member))
                        }
                    }
                }
            }
        },
    )
