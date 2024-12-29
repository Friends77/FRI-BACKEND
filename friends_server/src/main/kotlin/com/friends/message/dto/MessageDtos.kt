package com.friends.message.dto

import com.friends.message.entity.MessageType
import java.time.LocalDateTime

data class MessageResponseDto(
    val messageId: Long,
    val senderId: Long,
    val content: String,
    val type: MessageType,
    val createdAt: LocalDateTime,
)
