package com.friends.friendship.repository

import com.friends.friendship.entity.Friendship
import com.friends.member.entity.Member
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface FriendShipRepository :
    JpaRepository<Friendship, Long>,
    FriendShipCustomRepository {
    fun existsByRequesterAndReceiver(
        requester: Member,
        receiver: Member,
    ): Boolean

    fun findByRequesterIdAndReceiverId(
        requesterId: Long,
        receiverId: Long,
    ): Friendship?
}

interface FriendShipCustomRepository

class FriendShipCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : FriendShipCustomRepository
