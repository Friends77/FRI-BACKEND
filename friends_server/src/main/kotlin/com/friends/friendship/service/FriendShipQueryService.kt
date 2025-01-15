package com.friends.friendship.service

import com.friends.friendship.repository.FriendShipRepository
import com.friends.profile.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendShipQueryService(
    private val friendShipRepository: FriendShipRepository,
    private val profileRepository: ProfileRepository,
) {
    fun getFriendshipList(
        memberId: Long,
        lastFriendShipId: Long,
        size: Int,
    ) {
    }
}
