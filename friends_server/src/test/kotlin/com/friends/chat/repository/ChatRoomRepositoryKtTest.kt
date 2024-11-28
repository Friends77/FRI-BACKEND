package com.friends.chat.repository

import com.friends.chat.createTestChatRoom
import com.friends.member.createTestMember
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.MongoRepositoryTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@MongoRepositoryTest
class ChatRoomRepositoryKtTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
) : DescribeSpec(
        {

            isolationMode = IsolationMode.InstancePerLeaf
            val member = memberRepository.save(createTestMember())
            val chatRoom1 = chatRoomRepository.save(createTestChatRoom(createrId = member.id))

            afterEach { chatRoomRepository.delete(chatRoom1) }

            describe("getById 메서드는") {
                context("존재하는 채팅방 ID를 받으면") {
                    it("chatRoom을 반환한다") {
                        val chatRoom = chatRoomRepository.getById(chatRoom1.chatRoomId!!)
                        chatRoom.createrId shouldBe member.id
                    }
                }
            }
        },
    )
