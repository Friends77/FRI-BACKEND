package com.friends.recommendation.service

import com.friends.common.dto.ListBaseResponse
import com.friends.profile.dto.ProfileWithCategories
import com.friends.profile.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GlobalRecommendationService(
    private val profileRepository: ProfileRepository,
) {
    @Value("\${image.profile-base-url}")
    lateinit var profileBaseImageUrl: String

    fun getCategoryRecommendation(
        categoryIds: List<Long>,
        size: Int,
    ): ListBaseResponse<ProfileWithCategories> {
        val pageable = Pageable.ofSize(size)
        val profiles = profileRepository.findProfileWithCategoryIds(categoryIds, pageable)

        return ListBaseResponse(
            profiles.content.map {
                ProfileWithCategories(
                    it.id,
                    it.member.nickname,
                    it.imageUrl ?: profileBaseImageUrl,
                    it.interestTag.map { tag -> tag.category.id },
                )
            },
        )
    }
}
