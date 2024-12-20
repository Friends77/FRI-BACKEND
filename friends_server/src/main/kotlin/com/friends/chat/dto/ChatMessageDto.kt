package com.friends.chat.dto

import java.time.LocalDateTime

data class ChatReceiveMessageDto(
    val senderId: Long,
    val senderName: String,
    val senderProfileImageUrl: String,
    val message: String,
)

data class ChatSendMessageDto(
    val senderId: Long,
    val senderName: String,
    val senderProfileImageUrl: String,
    val message: String,
    val sendTime: LocalDateTime,
)
