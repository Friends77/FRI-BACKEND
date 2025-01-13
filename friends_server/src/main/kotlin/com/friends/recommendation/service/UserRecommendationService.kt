package com.friends.recommendation.service

import com.friends.common.dto.ListBaseResponse
import com.friends.profile.ProfileLocationNullException
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileWithDistanceDto
import com.friends.profile.repository.ProfileRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserRecommendationService(
    private val profileRepository: ProfileRepository,
) {
    fun getDistanceRecommendation(
        memberId: Long,
        distanceMeter: Double,
        size: Int,
    ): ListBaseResponse<ProfileWithDistanceDto> {
        val myProfile = profileRepository.findByMemberId(memberId) ?: throw ProfileNullResponseException()
        myProfile.location?.let { location ->
            return ListBaseResponse(
                profileRepository.findAllInDistance(
                    location.latitude,
                    location.longitude,
                    distanceMeter,
                    Pageable.ofSize(size),
                ),
            )
        } ?: throw ProfileLocationNullException()
    }
}
