package com.friends.friendship.service

import com.friends.friendship.entity.Friendship
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

@RepositoryTest
class FriendShipRepositiryTest(
    private val friendShipRepository: FriendShipRepository,
    private val memberRepository: MemberRepository,
) : DescribeSpec({
        lateinit var requester: Member
        lateinit var receiver1: Member
        lateinit var receiver2: Member
        lateinit var receiver3: Member
        lateinit var receiver4: Member
        lateinit var receiver5: Member

        beforeEach {
            requester = memberRepository.save(createTestMember(email = "test1"))
            receiver1 = memberRepository.save(createTestMember(email = "test2", nickname = "test1"))
            receiver2 = memberRepository.save(createTestMember(email = "test3", nickname = "test2"))
            receiver3 = memberRepository.save(createTestMember(email = "test4", nickname = "test3"))
            receiver4 = memberRepository.save(createTestMember(email = "test5", nickname = "test4"))
            receiver5 = memberRepository.save(createTestMember(email = "test6", nickname = "test5"))

            friendShipRepository.save(Friendship(0L, requester, receiver1, FriendshipStatusEnums.ACCEPT))
            friendShipRepository.save(Friendship(0L, requester, receiver2, FriendshipStatusEnums.BLOCK))
            friendShipRepository.save(Friendship(0L, receiver3, requester, FriendshipStatusEnums.BLOCK))
            friendShipRepository.save(Friendship(0L, receiver4, requester, FriendshipStatusEnums.ACCEPT))
        }

        describe("findFriendAndBlockedByMemberId 메서드는") {
            context("친구와 차단된 유저를 조회할 때") {
                it("친구와 차단된 유저를 조회한다.") {
                    val result = friendShipRepository.findFriendAndBlockedByMemberId(requester.id)
                    result.size shouldBe 4
                    result shouldContain receiver1
                    result shouldContain receiver2
                    result shouldContain receiver3
                    result shouldContain receiver4
                }
            }
        }
    })
