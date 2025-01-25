package com.friends.recommendation.service

import com.friends.category.dto.CategoryInfoResponse
import com.friends.chat.dto.ChatRoomRecommendationResponseDto
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.dto.ListBaseResponse
import com.friends.profile.dto.ProfileWithCategoriesResponseDto
import com.friends.profile.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GlobalRecommendationService(
    private val profileRepository: ProfileRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    @Value("\${image.chat-room-base-url}")
    private val chatBaseImageUrl: String,
    @Value("\${image.profile-base-url}")
    private val profileBaseImageUrl: String,
) {
    fun getRecommendationByUserCategory(
        categoryIds: List<Long>,
        size: Int,
    ): ListBaseResponse<ProfileWithCategoriesResponseDto> {
        val pageable = Pageable.ofSize(size)
        val profiles = profileRepository.findProfileWithCategoryIds(categoryIds, pageable)

        return ListBaseResponse(
            profiles.content.map {
                ProfileWithCategoriesResponseDto(
                    it.id,
                    it.member.nickname,
                    it.imageUrl ?: profileBaseImageUrl,
                    it.interestTag.map { tag -> tag.category.id },
                )
            },
        )
    }

    fun getRecommendationByChatCategory(
        categoryIds: List<Long>,
        size: Int,
    ): ListBaseResponse<ChatRoomRecommendationResponseDto> {
        val chatRooms = chatRoomRepository.findChatRoomWithCategoryIds(categoryIds, size)
        val result =
            chatRooms.map {
                ChatRoomRecommendationResponseDto(
                    it.id,
                    it.title,
                    it.imageUrl ?: chatBaseImageUrl,
                    it.categories.map { chatRoomCategory -> CategoryInfoResponse(chatRoomCategory.category.id) },
                    chatRoomMemberRepository.countByChatRoom(it),
                    chatRoomMemberRepository.findRepresentativeProfileByChatRoomId(it.id).map { member -> member.profile?.imageUrl ?: profileBaseImageUrl },
                    it.description,
                )
            }
        return ListBaseResponse(result)
    }
}
