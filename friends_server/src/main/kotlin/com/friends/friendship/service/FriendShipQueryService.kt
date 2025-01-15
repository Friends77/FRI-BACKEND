package com.friends.friendship.service

import com.friends.friendship.repository.FriendShipRepository
import com.friends.profile.dto.ProfileSimpleResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendShipQueryService(
    private val friendShipRepository: FriendShipRepository,
) {
    @Value("\${image.profile-base-url}")
    lateinit var profileBaseImageUrl: String

    fun getFriendshipList(
        memberId: Long,
    ): List<ProfileSimpleResponseDto> {
        // Friendship 조회
        val friendships = friendShipRepository.findAllFriendsByMemberId(memberId)

        // 친구만 추출
        val friends =
            friendships.map { friendship ->
                // 요청자와 수락자 중 "나"가 아닌 멤버의 ID를 추출
                if (friendship.requester.id == memberId) {
                    friendship.receiver
                } else {
                    friendship.requester
                }
            }

        // DTO 변환 및 정렬
        return friends
            .map { friend ->
                ProfileSimpleResponseDto(
                    memberId = friend.id,
                    nickname = friend.nickname,
                    imageUrl = friend.profile?.imageUrl ?: profileBaseImageUrl,
                    selfDescription = friend.profile?.selfDescription,
                )
            }.sortedBy { it.nickname }
    }
}
