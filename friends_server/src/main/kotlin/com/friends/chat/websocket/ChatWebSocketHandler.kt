package com.friends.chat.websocket

import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.chat.repository.ChatRoomMemberRepository
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
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) : TextWebSocketHandler() {
    // 채팅방 ID를 키로 하고, 참여하고 있는 member의 id를 value로 하는 Map
    private val connectedParticipants = ConcurrentHashMap<Long, MutableSet<Long>>()
    // memberId 를 키로 하고, WebSocketSession을 value로 하는 Map
    private val sessions = ConcurrentHashMap<Long, WebSocketSession>()

    private fun addParticipant(
        chatRoomId: Long,
        memberId: Long,
    ) {
        connectedParticipants.putIfAbsent(chatRoomId, mutableSetOf())
        connectedParticipants[chatRoomId]?.add(memberId)
    }

    private fun removeParticipant(
        chatRoomId: Long,
        memberId: Long,
    ) {
        connectedParticipants[chatRoomId]?.remove(memberId)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        /**
         * 웹소켓 연결 후 메세지를 수신받기 위해 sessions Map 에 저장합니다.
         */
        val memberId = getMemberId(session)
        sessions.putIfAbsent(memberId, session)

        /**
         * 메세지를 수신할 채팅방(참여중인 모든 채팅방)을 추가합니다.
         */
        val chatRoomMemberList = chatRoomMemberRepository.findAllByMemberId(memberId)
        chatRoomMemberList.forEach { chatRoomMember ->
            addParticipant(chatRoomMember.chatRoom.id, memberId)
        }
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
        connectedParticipants[chatRoomId]?.forEach { participantId ->
            sessions[participantId]?.sendMessage(TextMessage(JsonUtil.toJson(sendMessageDto)))
        }
        // TODO : 메세지 전송 실패 시 에러 처리
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        /**
         * 웹소켓 연결 종료 후 sessions Map 에서 제거합니다.
         */
        val memberId = getMemberId(session)
        sessions.remove(memberId)
        /**
         * 메세지를 수신하지 않도록 연결된 채팅방을 제거합니다.
         */
        val chatRoomMemberList = chatRoomMemberRepository.findAllByMemberId(memberId)
        chatRoomMemberList.forEach { chatRoomMember ->
            removeParticipant(chatRoomMember.chatRoom.id, memberId)
        }
    }

    private fun getMemberId(session: WebSocketSession): Long = session.attributes["MEMBER_ID"] as Long
}
