package com.friends.chat.websocket

import com.friends.chat.UnexpectedChatRoomException
import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.common.util.JsonUtil
import com.friends.message.entity.MessageType
import com.friends.message.service.MessageCommandService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatWebSocketHandler(
    private val messageCommandService: MessageCommandService,
) : TextWebSocketHandler() {
    companion object {
        private const val SEND_TIME_LIMIT = 2000 // 2초
        private const val BUFFER_SIZE_LIMIT = 1024 * 1024 // 1MB
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        try {
            val memberId = getMemberId(session)
            messageCommandService.setAllChatRoomsOnline(memberId, ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT))
        } catch (e: Exception) {
            session.close(CloseStatus.SERVER_ERROR)// 채팅방 연결 종료 후 에러 처리
            throw UnexpectedChatRoomException(e)
        }
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage,
    ) {
        val chatMessage = JsonUtil.fromJson<ChatReceiveMessageDto>(message.payload)
        val memberId = getMemberId(session)
        /**
         * 채팅 웹소켓을 통해 보내는 메세지는 TEXT 타입만 있다고 가정합니다.
         * 이미지의 경우 웹소켓이 아닌 REST API 를 통해 이미지를 업로드하고 이미지 URL 을 채팅방에 보내는 방식으로 구현합니다. // TODO : 채팅방 내에서 이미지 전송하는 API 구현
         */
        messageCommandService.sendMessage(
            chatMessage.chatRoomId,
            memberId,
            chatMessage.message,
            MessageType.TEXT,
        )
        // TODO : 아래 내용 리뷰 받고 수정
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        /**
         * 웹소켓 연결이 종료되면 온라인 유저 목록에서 제거됩니다.
         */
        val memberId = getMemberId(session)
        messageCommandService.setAllChatRoomsOffline(memberId, session)
        // TODO : 채팅방 나가기 실패 시 에러 처리
    }

    private fun getMemberId(session: WebSocketSession): Long = session.attributes["MEMBER_ID"] as Long
}
