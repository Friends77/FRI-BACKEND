package com.friends.friendship.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

data class Friendship(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    val id: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    @Column(name = "member_email")
    val memberEmail: String? = null,
    @Column(name = "friend_email")
    val friendEmail: String? = null,
    @Column(name =  "friendship_status")
    var friendshipStatus: FriendshipStatusEnums,
    @Column(name = "is_from")
    var isFrom: Boolean,
    ) :BaseModifiableEntity(){
        fun acceptFriendshipRequest(){
            friendshipStatus = FriendshipStatusEnums.ACCEPT
        }
        fun waitFriendshipRequest(){
            friendshipStatus = FriendshipStatusEnums.WAITING
        }
        fun blockFriendRequest(){
            friendshipStatus = FriendshipStatusEnums.BLOCK
        }
    }
