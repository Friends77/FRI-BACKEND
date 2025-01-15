package com.friends.friendship.repository

import com.friends.friendship.entity.Friendship
import com.friends.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface FriendShipRepository : JpaRepository<Friendship, Long> {
    fun existsByRequesterAndReceiver(
        requester: Member,
        receiver: Member,
    ): Boolean

    fun findByRequesterIdAndReceiverId(
        requesterId: Long,
        receiverId: Long,
    ): Friendship?
}
