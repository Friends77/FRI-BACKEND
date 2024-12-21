package com.friends.message.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatSendMessageDto
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.util.JsonUtil
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
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
        memberId: Long,
        session: WebSocketSession,
    ) {
        chatRooms[chatRoomId]?.remove(session) // 세션 제거

        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: return // 채팅방이 없을 경우 무시
        val member = memberRepository.findById(memberId).orElse(null) ?: return // 멤버가 없을 경우 무시
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: return // 채팅방 멤버가 아닐 경우 무시

        // 마지막으로 읽은 메세지 ID 업데이트 (채팅방에 메세지가 없다면 무시)
        messageRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)?.let {
            chatRoomMember.lastReadMessageId = it.id
        }
    }

    fun sendMessage(
        chatRoomId: Long,
        memberId: Long,
        message: String,
        type: MessageType,
    ) {
        val sessions = chatRooms[chatRoomId]

        /**
         * 메세지를 보낼 때마다 보낸 유저와 채팅방이 있는지 DB 에 확인합니다.
         * 이 과정이 비효율적일 경우 아래 프록시 객체를 생성하여 메세지를 보내는 로직을 고려합니다.
         * 프록시 객체는 DB 에서 채팅방과 유저 정보를 요청하지 않지만, DB 에 데이터가 있는지 없는지 확인할 수 없습니다.
         *
         * val chatRoom = entityManager.getReference(ChatRoom::class.java, chatRoomId)
         * val sender = entityManager.getReference(Member::class.java, message.senderId)
         */
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
        val sender = memberRepository.findById(message.senderId).orElseThrow { MemberNotFoundException() }

        val savedMessage = messageRepository.save(Message.of(chatRoom, sender, message.message, type))
        val sendMessageDto =
            ChatSendMessageDto(
                senderId = sender.id,
                senderName = sender.nickname,
                senderProfileImageUrl = sender.imageUrl ?: "default image url", // TODO : member 와 profile 에 imageUrl 이 중복되어 있는 것을 확인 -> 둘 중 하나로 통일하고 non-null 로 변경 (회원가입 시 주어지지 않는다면 default image url 로 설정)
                message = savedMessage.content,
                sendTime = savedMessage.createdAt,
                type = savedMessage.type,
            )
        sessions?.forEach { session ->
            if (session.isOpen) {
                session.sendMessage(TextMessage(JsonUtil.toJson(sendMessageDto)))
            }
        }
    }
}
