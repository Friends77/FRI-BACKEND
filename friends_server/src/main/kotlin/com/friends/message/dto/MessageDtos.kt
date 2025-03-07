package com.friends.message.dto

import com.friends.message.entity.MessageType

data class MessageResponseDto(
    val messageId: Long,
    val senderId: Long,
    val profileImageUrl: String,
    val nickname: String,
    val content: String,
    val type: MessageType,
    val createdAt: Long,
)
