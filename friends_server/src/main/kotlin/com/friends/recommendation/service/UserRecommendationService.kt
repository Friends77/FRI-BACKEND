package com.friends.recommendation.service

import com.friends.common.dto.ListBaseResponse
import com.friends.friendship.entity.FriendshipRequestStatusEnums
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.friendship.repository.FriendShipRepository
import com.friends.member.entity.Member
import com.friends.profile.ProfileLocationNullException
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileSimpleResponseDto
import com.friends.profile.dto.ProfileWithDistanceResponseDto
import com.friends.profile.repository.ProfileRepository
import com.friends.recommendation.dto.LonginRecommendationByRandom
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserRecommendationService(
    private val profileRepository: ProfileRepository,
    private val friendShipRepository: FriendShipRepository,
) {
    @Value("\${image.profile-base-url}")
    lateinit var profileBaseImageUrl: String

    fun getDistanceRecommendation(
        memberId: Long,
        distanceMeter: Double,
        size: Int,
    ): ListBaseResponse<ProfileWithDistanceResponseDto> {
        val myProfile = profileRepository.findByMemberId(memberId) ?: throw ProfileNullResponseException()
        val location = myProfile.location ?: throw ProfileLocationNullException()
        val result =
            profileRepository
                .findAllInDistance(
                    location.latitude,
                    location.longitude,
                    distanceMeter,
                    Pageable.ofSize(size),
                ).map {
                    ProfileWithDistanceResponseDto(
                        id = it.id,
                        nickname = it.nickname,
                        imageUrl = it.imageUrl ?: profileBaseImageUrl,
                        distance = it.distance,
                    )
                }
        return ListBaseResponse(result)
    }

    fun getRecommendationByRandom(
        size: Int,
        memberId: Long,
    ): ListBaseResponse<LonginRecommendationByRandom> {
        val myProfile = profileRepository.findByMemberId(memberId) ?: throw ProfileNullResponseException()
        val member = myProfile.member
        val friends = friendShipRepository.findFriendshipByMemberIdAndNickname(memberId, null)
        val result =
            profileRepository
                .findRandomProfileExcludeFriend(Pageable.ofSize(size), friends, member)
                .map {
                    LonginRecommendationByRandom(
                        ProfileSimpleResponseDto(
                            it.member.id,
                            it.member.nickname,
                            it.imageUrl ?: profileBaseImageUrl,
                            it.selfDescription,
                        ),
                        memberFriendshipStatus(member, it.member),
                    )
                }
        return ListBaseResponse(result)
    }

    private fun memberFriendshipStatus(
        member: Member,
        friend: Member,
    ): FriendshipRequestStatusEnums {
        val requestFriendship = friendShipRepository.findByRequesterAndReceiver(member, friend)
        if (requestFriendship != null) {
            return if (requestFriendship.getFriendshipStatus() == FriendshipStatusEnums.WAITING) {
                FriendshipRequestStatusEnums.REQUESTED
            } else {
                FriendshipRequestStatusEnums.UNAVAILABLE
            }
        }
        val receiveFriendship = friendShipRepository.findByRequesterAndReceiver(friend, member)
        if (receiveFriendship != null) {
            return if (receiveFriendship.getFriendshipStatus() == FriendshipStatusEnums.WAITING) {
                FriendshipRequestStatusEnums.RECEIVED
            } else {
                FriendshipRequestStatusEnums.UNAVAILABLE
            }
        }
        return FriendshipRequestStatusEnums.AVAILABLE
    }
}
