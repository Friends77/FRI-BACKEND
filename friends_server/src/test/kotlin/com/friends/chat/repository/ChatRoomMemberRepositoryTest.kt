package com.friends.chat.repository

import com.friends.TEST_SIZE
import com.friends.chat.createTestChatRoom
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.member.MEMBER_OTHER_EMAIL
import com.friends.member.MEMBER_OTHER_NICKNAME
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@RepositoryTest
class ChatRoomMemberRepositoryTest(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) : DescribeSpec({
        lateinit var member1: Member
        lateinit var member2: Member
        lateinit var chatRoom1: ChatRoom
        lateinit var chatRoom2: ChatRoom
        lateinit var chatRoomMember: ChatRoomMember
        lateinit var chatRoomMember2: ChatRoomMember
        lateinit var chatRoomMember3: ChatRoomMember
        beforeEach {
            member1 = memberRepository.save(createTestMember())
            member2 = memberRepository.save(createTestMember(email = MEMBER_OTHER_EMAIL, nickname = MEMBER_OTHER_NICKNAME))
            chatRoom1 = chatRoomRepository.save(createTestChatRoom(manager = member1))
            chatRoom2 = chatRoomRepository.save(createTestChatRoom(manager = member2))
            chatRoomMember = chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom1, member1))
            chatRoomMember2 = chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom1, member2))
            chatRoomMember3 = chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom2, member2))
        }

        describe("countByChatRoomId 메서드는") {
            context("존재하는 채팅방 ID를 받으면") {
                it("chatRoomMember의 수를 반환한다") {
                    chatRoomMemberRepository.countByChatRoom(chatRoom1) shouldBe 2
                }
            }
        }

        describe("findByChatRoomIdAndMemberId 메서드는") {
            context("회원 ID를 받으면") {
                it("chatRoomMember를 반환한다") {
                    chatRoomMemberRepository.findChatRoomByMemberListIn(listOf(member1, member2)) shouldBe listOf(chatRoom1, chatRoom2)
                }
            }
        }

        describe("sliceChatRoomIdByMember 메서드는") {
            context("memberId만 받으면") {
                it("chatRoomMember를 전부 반환한다") {
                    chatRoomMemberRepository.sliceChatRoomIdByMember(member2.id, listOf(), TEST_SIZE, null).content shouldBe listOf(chatRoom2, chatRoom1)
                }
            }

            context("채팅방 ID 리스트를 받으면") {
                it("chatRoomMember를 전부 반환한다") {
                    chatRoomMemberRepository.sliceChatRoomIdByMember(member2.id, listOf(chatRoom1), TEST_SIZE, null).content shouldBe listOf(chatRoom1)
                }
            }

            context("사이즈를 받으면") {
                it("chatRoomMember를 사이즈만큼 반환한다") {
                    chatRoomMemberRepository.sliceChatRoomIdByMember(member2.id, listOf(), 1, null).content shouldBe listOf(chatRoom2)
                }
            }

            context("회원 ID와 마지막 채팅방 ID를 받으면") {
                it("chatRoomMember를 반환한다") {
                    chatRoomMemberRepository.sliceChatRoomIdByMember(member2.id, listOf(), TEST_SIZE, chatRoom2.id).content shouldBe listOf(chatRoom1)
                }
            }
        }
    })
