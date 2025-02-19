package com.friends.alarm

import com.friends.alarm.entity.AlarmType

data class AlarmResponseDto(
    val id: Long,
    val type: AlarmType,
    val message: String,
    val senderId: Long,
    val senderProfileImage: String,
    val nickname: String,
    val receiverId: Long,
    val invitedChatRoomId: Long? = null,
    val createdAt: Long,
)
