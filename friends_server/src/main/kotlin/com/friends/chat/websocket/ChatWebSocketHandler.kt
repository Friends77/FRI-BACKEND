package com.friends.chat.websocket

import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.chat.dto.ChatSendMessageDto
import com.friends.common.util.JsonUtil
import com.friends.message.service.MessageService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.LocalDateTime

@Component
class ChatWebSocketHandler(
    private val messageService: MessageService,
) : TextWebSocketHandler() {
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatRoomId = getChatRoomId(session)
        messageService.connectChatRoom(chatRoomId, session)
        // TODO : 채팅방 입장 실패 시 에러 처리
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        val chatMessage = JsonUtil.fromJson<ChatReceiveMessageDto>(message.payload)
        val chatRoomId = getChatRoomId(session)
        val chatSendMessageDto =
            ChatSendMessageDto(
                senderId = chatMessage.senderId,
                senderName = chatMessage.senderName,
                senderProfileImageUrl = chatMessage.senderProfileImageUrl,
                message = chatMessage.message,
                sendTime = LocalDateTime.now(),
            )
        messageService.sendMessage(chatRoomId, chatSendMessageDto)
        // TODO : 메세지 전송 실패 시 에러 처리
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        val chatRoomId = getChatRoomId(session)
        messageService.disconnectChatRoom(chatRoomId, session)
        // TODO : 채팅방 나가기 실패 시 에러 처리
    }

    // 채팅방 ID를 URI에서 추출하는 함수
    private fun getChatRoomId(session: WebSocketSession): Long {
        val uri = session.uri.toString()
        return uri.substringAfterLast("/").toLong()
        // TODO : 채팅방 아이디 얻기 실패 시 에러 처리
    }
}
