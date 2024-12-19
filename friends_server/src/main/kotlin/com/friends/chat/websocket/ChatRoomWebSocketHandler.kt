package com.friends.chat.websocket

import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.chat.dto.ChatSendMessageDto
import com.friends.common.util.JsonUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Component
class ChatRoomWebSocketHandler : TextWebSocketHandler() {
    private val log = LoggerFactory.getLogger(ChatRoomWebSocketHandler::class.java)
    // 채팅방 ID를 키로 하고, 각 채팅방의 세션을 Set으로 저장
    private val chatRooms: MutableMap<String, MutableSet<WebSocketSession>> = ConcurrentHashMap()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val chatRoomId = getChatRoomId(session)
        chatRooms.computeIfAbsent(chatRoomId) { CopyOnWriteArraySet() }
        chatRooms[chatRoomId]?.add(session)
        log.debug("User connected to chat room: $chatRoomId")
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        try {
            val chatMessage = JsonUtil.fromJson<ChatReceiveMessageDto>(message.payload)
            val chatRoomId = getChatRoomId(session)
            val sessions = chatRooms[chatRoomId]
            sessions?.forEach { s ->
                if (s.isOpen) {
                    val chatSendMessageDto = ChatSendMessageDto.from(chatMessage, LocalDateTime.now())
                    s.sendMessage(TextMessage(JsonUtil.toJson(chatSendMessageDto)))
                }
            }
            log.debug("Received message: $chatMessage")
        } catch (e: Exception) {
            log.error("Failed to parse message", e)
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        val chatRoomId = getChatRoomId(session)
        chatRooms[chatRoomId]?.remove(session)
        log.debug("User disconnected from chat room: $chatRoomId")
    }

    // 채팅방 ID를 URI에서 추출하는 함수
    private fun getChatRoomId(session: WebSocketSession): String {
        val uri = session.uri.toString()
        return uri.substringAfterLast("/")
    }
}
