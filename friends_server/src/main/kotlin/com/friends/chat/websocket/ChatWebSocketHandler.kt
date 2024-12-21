package com.friends.chat.websocket

import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.common.util.JsonUtil
import com.friends.message.entity.MessageType
import com.friends.message.service.MessageService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

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
        val memberId = getMemberId(session)
        /**
         * 채팅 웹소켓을 통해 보내는 메세지는 TEXT 타입만 있다고 가정합니다.
         * 이미지의 경우 웹소켓이 아닌 REST API 를 톹ㅇ해 이미지를 업로드하고 이미지 URL 을 채팅방에 보내는 방식으로 구현합니다. // TODO : 채팅방 내에서 이미지 전송하는 API 구현
         */
        messageService.sendMessage(chatRoomId, memberId, chatMessage.message, MessageType.TEXT)
        // TODO : 메세지 전송 실패 시 에러 처리
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        val chatRoomId = getChatRoomId(session)
        val memberId = getMemberId(session)
        messageService.disconnectChatRoom(chatRoomId, memberId, session)
        // TODO : 채팅방 나가기 실패 시 에러 처리
    }

    // 채팅방 ID를 URI에서 추출하는 함수
    private fun getChatRoomId(session: WebSocketSession): Long {
        val uri = session.uri.toString()
        return uri.substringAfterLast("/").toLong()
        // TODO : 채팅방 아이디 얻기 실패 시 에러 처리
    }

    private fun getMemberId(session: WebSocketSession): Long = session.attributes["MEMBER_ID"] as Long
}
