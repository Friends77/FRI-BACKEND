package com.friends.friendship.repository

import com.friends.common.util.getList
import com.friends.friendship.entity.Friendship
import com.friends.friendship.entity.FriendshipStatusEnums
import com.friends.member.entity.Member
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRepository :
    JpaRepository<Friendship, Long>,
    FriendshipCustomRepository

interface FriendshipCustomRepository {
    fun findFriendshipByMemberIdAndNickname(
        memberId: Long,
        nickname: String,
    ): List<Member>
}

class FriendshipCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : FriendshipCustomRepository {
    override fun findFriendshipByMemberIdAndNickname(
        memberId: Long,
        nickname: String,
    ): List<Member> {
        val result1 =
            kotlinJdslJpqlExecutor.getList {
                select(path(Friendship::requestMember))
                    .from(entity(Friendship::class))
                    .where(
                        and(
                            path(Friendship::receiveMember).path(Member::id).equal(memberId),
                            path(Friendship::requestMember).path(Member::nickname).like("%$nickname%"),
                            path(Friendship::friendshipStatus).equal(FriendshipStatusEnums.ACCEPT),
                        ),
                    )
            }
        val result2 =
            kotlinJdslJpqlExecutor.getList {
                select(path(Friendship::receiveMember))
                    .from(entity(Friendship::class))
                    .where(
                        and(
                            path(Friendship::requestMember).path(Member::id).equal(memberId),
                            path(Friendship::receiveMember).path(Member::nickname).like("%$nickname%"),
                            path(Friendship::friendshipStatus).equal(
                                FriendshipStatusEnums.ACCEPT,
                            ),
                        ),
                    )
            }

        return result1 + result2
    }
}
