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
) {
    companion object {
        fun from(
            chatReceiveMessageDto: ChatReceiveMessageDto,
            sendTime: LocalDateTime,
        ): ChatSendMessageDto =
            ChatSendMessageDto(
                senderId = chatReceiveMessageDto.senderId,
                senderName = chatReceiveMessageDto.senderName,
                senderProfileImageUrl = chatReceiveMessageDto.senderProfileImageUrl,
                message = chatReceiveMessageDto.message,
                sendTime = sendTime,
            )
    }
}
