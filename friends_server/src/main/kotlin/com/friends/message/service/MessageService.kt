package com.friends.message.service

import com.friends.chat.dto.ChatSendMessageDto
import com.friends.common.util.JsonUtil
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Service
class MessageService(
    private val memberRepository: MemberRepository,
) {
    // 채팅방 ID를 키로 하고, 각 채팅방의 세션을 Set으로 저장
    private val chatRooms: MutableMap<Long, MutableSet<WebSocketSession>> = ConcurrentHashMap()

    fun connectChatRoom(
        chatRoomId: Long,
        session: WebSocketSession,
    ) {
        chatRooms.computeIfAbsent(chatRoomId) { CopyOnWriteArraySet() }
        chatRooms[chatRoomId]?.add(session)
    }

    fun disconnectChatRoom(
        chatRoomId: Long,
        session: WebSocketSession,
    ) {
        chatRooms[chatRoomId]?.remove(session)
    }

    fun sendMessage(
        chatRoomId: Long,
        message: ChatSendMessageDto,
    ) {
        val sessions = chatRooms[chatRoomId]
        sessions?.forEach { session ->
            if (session.isOpen) {
                session.sendMessage(TextMessage(JsonUtil.toJson(message)))
            }
        }
    }
}
