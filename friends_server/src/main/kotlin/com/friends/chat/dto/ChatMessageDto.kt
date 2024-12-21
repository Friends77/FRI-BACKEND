package com.friends.chat.dto

import com.friends.message.entity.MessageType
import java.time.LocalDateTime

/**
 * 채팅 웹소켓에서 메세지 전송은 로그인된 유저만 이용할 수 있기 때문에 senderId는 SecurityContextHolder에서 가져옵니다.
 */
data class ChatReceiveMessageDto(
    val message: String,
)

data class ChatSendMessageDto(
    val senderId: Long,
    val message: String,
    val sendTime: LocalDateTime,
    val type: MessageType,
)
