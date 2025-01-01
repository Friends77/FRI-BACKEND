package com.friends.chat.websocket

import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.common.util.JsonUtil
import com.friends.message.entity.MessageType
import com.friends.message.service.MessageCommandService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatWebSocketHandler(
    private val messageCommandService: MessageCommandService,
) : TextWebSocketHandler() {
    // 채팅방 ID를 키로 하고, 참여하고 있는 member의 id를 value로 하는 Map
    private val participants = ConcurrentHashMap<Long, MutableSet<Long>>()
    // memberId 를 키로 하고, WebSocketSession을 value로 하는 Map
    private val sessions = ConcurrentHashMap<Long, WebSocketSession>()

    fun addParticipant(
        chatRoomId: Long,
        memberId: Long,
    ) {
        participants.putIfAbsent(chatRoomId, mutableSetOf())
        participants[chatRoomId]?.add(memberId)
    }

    fun removeParticipant(
        chatRoomId: Long,
        memberId: Long,
    ) {
        participants[chatRoomId]?.remove(memberId)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val memberId = getMemberId(session)
        sessions.putIfAbsent(memberId, session)
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        val chatMessage = JsonUtil.fromJson<ChatReceiveMessageDto>(message.payload)
        val chatRoomId = chatMessage.chatRoomId
        val memberId = getMemberId(session)
        /**
         * 채팅 웹소켓을 통해 보내는 메세지는 TEXT 타입만 있다고 가정합니다.
         * 이미지의 경우 웹소켓이 아닌 REST API 를 톹ㅇ해 이미지를 업로드하고 이미지 URL 을 채팅방에 보내는 방식으로 구현합니다. // TODO : 채팅방 내에서 이미지 전송하는 API 구현
         */
        val sendMessageDto = messageCommandService.saveMessage(chatRoomId, memberId, chatMessage.message, MessageType.TEXT)
        /**
         * 채팅방에 참여하고 있는 모든 유저에게 메세지를 전송합니다.
         */
        participants[chatRoomId]?.forEach { participantId ->
            sessions[participantId]?.sendMessage(TextMessage(JsonUtil.toJson(sendMessageDto)))
        }
        // TODO : 메세지 전송 실패 시 에러 처리
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        val memberId = getMemberId(session)
        sessions.remove(memberId)
        // TODO : 채팅방 나가기 실패 시 에러 처리
    }

    private fun getMemberId(session: WebSocketSession): Long = session.attributes["MEMBER_ID"] as Long
}
