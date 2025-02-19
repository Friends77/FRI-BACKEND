package com.friends.recommendation.service

import com.friends.friendship.entity.Friendship
import com.friends.friendship.entity.FriendshipRequestStatusEnums
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.createTestMember
import com.friends.profile.createTestProfile
import com.friends.profile.repository.ProfileRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class UserRecommendationServiceIntegrationTest :
    BehaviorSpec({
        val profileRepository = mockk<ProfileRepository>()
        val friendShipRepository = mockk<FriendShipRepository>()
        val userRecommendationService = UserRecommendationService(profileRepository, friendShipRepository)

        given("getRecommendationByRandom 테스트") {
            val requester = createTestMember()
            val requesterProfile = createTestProfile(requester)
            val member = createTestMember(email = "test2")
            val memberProfile = createTestProfile(member)
            every { friendShipRepository.findFriendshipByMemberIdAndNickname(any(), any()) } returns emptyList()
            `when`("랜덤 추천을 요청 후 해당 유저에게 친구 요청을 보낸 경우") {
                every { profileRepository.findByMemberId(any()) } returns requesterProfile
                every { profileRepository.findRandomProfileExcludeFriend(any(), any(), any()) } returns listOf(memberProfile)
                every { friendShipRepository.findByRequesterAndReceiver(any(), any()) } returns Friendship(0L, requester, member, FriendshipStatusEnums.WAITING)
                every { friendShipRepository.findByRequesterAndReceiver(any(), any()) } returns Friendship(0L, requester, member, FriendshipStatusEnums.WAITING)
                val result = userRecommendationService.getRecommendationByRandom(3, requester.id).content[0]
                then("REQUESTED가 반환되어야 한다.") {
                    result.type shouldBe FriendshipRequestStatusEnums.REQUESTED
                }
            }

            `when`("랜덤 추천을 요청 후 해당 유저로부터 친구 요청을 받은 경우") {
                every { profileRepository.findByMemberId(any()) } returns memberProfile
                every { profileRepository.findRandomProfileExcludeFriend(any(), any(), any()) } returns listOf(requesterProfile)
                every { friendShipRepository.findByRequesterAndReceiver(member, requester) } returns null
                every { friendShipRepository.findByRequesterAndReceiver(requester, member) } returns Friendship(0L, requester, member, FriendshipStatusEnums.WAITING)
                val result = userRecommendationService.getRecommendationByRandom(3, member.id).content[0]
                then("RECEIVED가 반환되어야 한다.") {
                    result.type shouldBe FriendshipRequestStatusEnums.RECEIVED
                }
            }

            `when`("랜덤 추천을 요청 후 유저와 관계가 없는 경우") {
                every { profileRepository.findByMemberId(any()) } returns requesterProfile
                every { profileRepository.findRandomProfileExcludeFriend(any(), any(), any()) } returns listOf(memberProfile)
                every { friendShipRepository.findByRequesterAndReceiver(any(), any()) } returns null
                then("AVAILABLE이 반환되어야 한다.") {
                    val result = userRecommendationService.getRecommendationByRandom(3, requester.id).content[0].type
                    result shouldBe FriendshipRequestStatusEnums.AVAILABLE
                }
            }
        }
    })
