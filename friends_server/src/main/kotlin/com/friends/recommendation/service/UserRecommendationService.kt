package com.friends.recommendation.service

import com.friends.common.dto.ListBaseResponse
import com.friends.friendship.repository.FriendShipRepository
import com.friends.profile.ProfileLocationNullException
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileSimpleResponseDto
import com.friends.profile.dto.ProfileWithDistanceResponseDto
import com.friends.profile.repository.ProfileRepository
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
    ): ListBaseResponse<ProfileSimpleResponseDto> {
        val myProfile = profileRepository.findByMemberId(memberId) ?: throw ProfileNullResponseException()
        val friends = friendShipRepository.findFriendshipByMemberIdAndNickname(memberId, null)
        val result =
            profileRepository
                .findRandomProfileExcludeFriend(Pageable.ofSize(size), friends, myProfile.member)
                .map {
                    ProfileSimpleResponseDto(
                        it.member.id,
                        it.member.nickname,
                        it.imageUrl ?: profileBaseImageUrl,
                        it.selfDescription,
                    )
                }
        return ListBaseResponse(result)
    }
}
