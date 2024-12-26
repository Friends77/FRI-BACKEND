package com.friends.friendship.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Friendship(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    val id: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    val memberEmail: String? = null,
    @Column(nullable = false)
    val friendEmail: String,
    private var friendshipStatus: FriendshipStatusEnums,
    var isFrom: Boolean,
    //상대방 아이디
    var counterpartId: Long,
) : BaseModifiableEntity() {
    fun acceptFriendshipRequest() {
        friendshipStatus = FriendshipStatusEnums.ACCEPT
    }

    fun waitFriendshipRequest() {
        friendshipStatus = FriendshipStatusEnums.WAITING
    }

    fun blockFriendRequest() {
        friendshipStatus = FriendshipStatusEnums.BLOCK
    }
}
