package com.friends.chat.repository

import com.friends.chat.createTestChatRoom
import com.friends.chat.createTestChatRoomMember
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class ChatRoomMemberRepositoryTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) : DescribeSpec(
        {

            isolationMode = IsolationMode.InstancePerLeaf
            lateinit var member: Member
            lateinit var member2: Member
            lateinit var chatRoom1: ChatRoom
            lateinit var chatRoomMember1: ChatRoomMember

            beforeEach {
                member = memberRepository.save(createTestMember())
                member2 = memberRepository.save(createTestMember(email = "othrtEmail@naver.com"))
                chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member))
                chatRoomMember1 = chatRoomMemberRepository.save(createTestChatRoomMember(chatRoom1, member))
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

            describe("existsByMemberIdAndChatRoomId") {
                context("존재하는 ChatRoomId와 MemberId가 들어오는 경우") {
                    it("true를 반환한다.") {
                        chatRoomMemberRepository.existsByMemberIdAndChatRoomId(member.id, chatRoom1.id) shouldBe true
                    }
                }

                context("존재하지 않는 ChatRoomId와 MemberId가 들어오는 경우") {
                    it("false를 반환한다.") {
                        chatRoomMemberRepository.existsByMemberIdAndChatRoomId(member2.id, chatRoom1.id) shouldBe false
                    }
                }
            }
        },
    )
