package com.friends.chat.websocket

import com.friends.chat.UnexpectedChatRoomException
import com.friends.chat.dto.ChatReceiveMessageDto
import com.friends.chat.dto.ChatSendMessageDto
import com.friends.common.util.JsonUtil
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.friends.message.service.MessageCommandService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.ExecutorService

@Component
class ChatWebSocketHandler(
    private val messageCommandService: MessageCommandService,
    private val executor: ExecutorService,
) : TextWebSocketHandler() {
    // 채팅방 ID를 키로 하고, 참여하고 있는 member의 id를 value로 하는 Map
    private val connectedParticipants = ConcurrentHashMap<Long, MutableSet<Long>>()
    // memberId 를 키로 하고, WebSocketSession을 value로 하는 Map
    private val sessions = ConcurrentHashMap<Long, MutableSet<WebSocketSession>>()

    private fun addParticipant(
        chatRoomId: Long,
        memberId: Long,
    ) {
        connectedParticipants
            .computeIfAbsent(chatRoomId) {
                ConcurrentHashMap.newKeySet()
            }.add(memberId)
    }

    private fun removeParticipant(
        chatRoomId: Long,
        memberId: Long,
    ) {
        connectedParticipants[chatRoomId]?.remove(memberId)
    }

    private fun addSession(
        memberId: Long,
        session: WebSocketSession,
    ) {
        val userSessions =
            sessions.computeIfAbsent(memberId) {
                ConcurrentHashMap.newKeySet()
            }
        userSessions.add(session)
    }

    private fun removeSession(
        memberId: Long,
        session: WebSocketSession,
    ) {
        sessions[memberId]?.remove(session)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        try {
            val chatRoomId = getChatRoomId(session)
            chatRooms.computeIfAbsent(chatRoomId) { CopyOnWriteArraySet() }
            chatRooms[chatRoomId]?.add(session)
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
        val chatRoomId = chatMessage.chatRoomId
        val memberId = getMemberId(session)
        /**
         * 채팅 웹소켓을 통해 보내는 메세지는 TEXT 타입만 있다고 가정합니다.
         * 이미지의 경우 웹소켓이 아닌 REST API 를 통해 이미지를 업로드하고 이미지 URL 을 채팅방에 보내는 방식으로 구현합니다. // TODO : 채팅방 내에서 이미지 전송하는 API 구현
         */
        val sendMessageDto = messageCommandService.saveMessage(chatRoomId, memberId, chatMessage.message, MessageType.TEXT)
        /**
         * 채팅방에 참여하고 있는 모든 유저의 디바이스에 메세지를 전송합니다.
         */
        connectedParticipants[chatRoomId]?.forEach { participantId ->
            sessions[participantId]?.forEach { participantSession ->
                participantSession.sendMessage(TextMessage(JsonUtil.toJson(sendMessageDto)))
            }
        }
        // TODO : 아래 내용 리뷰 받고 수정
    }

    fun sendMessage(
        chatRoomId: Long,
        message: Message,
    ) {
        val sessions = chatRooms[chatRoomId]
        sessions?.forEach { session ->
            CompletableFuture // 비동기 처리
                .supplyAsync(
                    {
                        if (session.isOpen) {
                            try {
                                session.sendMessage(TextMessage(JsonUtil.toJson(ChatSendMessageDto(message.sender.id, message.content, message.createdAt, message.type))))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    executor,
                )
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus,
    ) {
        /**
         * 웹소켓 연결 종료 후 sessions Map 에서 제거합니다.
         */
        val memberId = getMemberId(session)
        removeSession(memberId, session)
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
