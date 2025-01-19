package com.friends.friendship.entity

enum class FriendshipStatusEnums(
    val description: String,
) {
    ACCEPT("친구요청을 수락했습니다"),
    WAITING("요청 대기중인 상태입니다"),
    BLOCK("친구요청을 거절당했습니다"),
}
