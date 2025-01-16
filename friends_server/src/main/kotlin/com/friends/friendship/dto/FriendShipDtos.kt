package com.friends.friendship.dto

data class FriendShipRequestDto(
    val receiverId: Long,
)

data class FriendShipReceiveDto(
    val requesterId: Long,
)
