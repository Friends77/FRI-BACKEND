package com.friends.chat.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.entity.ChatRoom
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.Optional

@RepositoryTest
class ChatRoomRepositoryKtTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
) : DescribeSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            lateinit var member: Member
            lateinit var chatRoom1: ChatRoom
            beforeEach {
                member = memberRepository.save(createTestMember())
                chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member))
            }

            describe("getByChatRoomId 메서드는") {
                context("존재하는 채팅방 ID를 받으면") {
                    it("chatRoom을 반환한다") {
                        val chatRoom = chatRoomRepository.getByChatRoomId(chatRoom1.id)
                        chatRoom.id shouldBe chatRoom1.id
                        chatRoom.manager.id shouldBe member.id
                    }
                }

                context("존재하지 않는 채팅방 ID를 받으면") {
                    it("null을 반환한다") {
                        val chatRoom = chatRoomRepository.findById(0)
                        chatRoom shouldBe Optional.empty()
                    }
                }
            }
        },
    )
