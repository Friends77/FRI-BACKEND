package com.friends.friendship.service

import com.friends.friendship.repository.FriendShipRepository
import com.friends.profile.dto.ProfileSimpleResponseDto
import com.friends.profile.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendShipQueryService(
    private val friendShipRepository: FriendShipRepository,
    private val profileRepository: ProfileRepository,
) {
    @Value("\${image.profile-base-url}")
    lateinit var profileBaseImageUrl: String

    fun getFriendshipList(
        memberId: Long,
    ): List<ProfileSimpleResponseDto> {
        // Friendship 조회
        val friendships = friendShipRepository.findAllFriendsByMemberId(memberId)

        // 친구의 memberId만 추출
        val friendIds =
            friendships.map { friendship ->
                // 요청자와 수락자 중 "나"가 아닌 멤버의 ID를 추출
                if (friendship.requester.id == memberId) {
                    friendship.receiver.id
                } else {
                    friendship.requester.id
                }
            }

        // Profile 조회
        val profiles = profileRepository.findAllById(friendIds)

        // DTO 변환 및 정렬
        return profiles
            .map { profile ->
                ProfileSimpleResponseDto(
                    memberId = profile.member.id,
                    nickname = profile.member.nickname,
                    imageUrl = profile.imageUrl ?: profileBaseImageUrl,
                    selfDescription = profile.selfDescription,
                )
            }.sortedBy { it.nickname }
    }
}
