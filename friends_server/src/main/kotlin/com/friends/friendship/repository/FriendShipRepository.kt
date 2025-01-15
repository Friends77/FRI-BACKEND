package com.friends.friendship.repository

import com.friends.common.util.getList
import com.friends.friendship.entity.Friendship
import com.friends.friendship.entity.FriendshipStatusEnums
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

interface FriendShipCustomRepository {
    fun findAllFriendsByMemberId(
        memberId: Long,
    ): List<Friendship>
}

class FriendShipCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : FriendShipCustomRepository {
    override fun findAllFriendsByMemberId(memberId: Long): List<Friendship> =
        kotlinJdslJpqlExecutor
            .getList {
                select(entity(Friendship::class))
                    .from(entity(Friendship::class))
                    .where(
                        and(
                            or(
                                path(Friendship::requester).path(Member::id).equal(memberId),
                                path(Friendship::receiver).path(Member::id).equal(memberId),
                            ),
                            path(Friendship::getFriendshipStatus).equal(FriendshipStatusEnums.ACCEPT),
                        ),
                    )
            }
}
