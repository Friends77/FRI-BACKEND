package com.friends.chat.entity

import com.friends.common.entity.BaseMongoTimeEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("chat_room")
class ChatRoom(
    var title: String,
    val createrId: Long,
    var imageUrl: String?, // 채팅방 이미지
    var categories: List<String> = listOf(),
    override var createdAt: LocalDateTime? = null,
) : BaseMongoTimeEntity() {
    @Id
    var chatRoomId: String? = null
        private set
    var participants: List<Long> = listOf(createrId)
        private set
    var messages: List<Message> = listOf()
        private set
    var lastMessageContent: String? = null
        private set
    var lastMessageSentTime: LocalDateTime? = null
        private set

    fun addParticipant(memberId: Long) {
        participants = participants.toMutableList().also { it.add(memberId) }
    }

    fun removeParticipant(memberId: Long) {
        participants = participants.toMutableList().also { it.remove(memberId) }
    }

    fun addMessage(message: Message) {
        messages = messages.toMutableList().also { it.add(message) }
    }

    fun updateLastMessage(message: Message) {
        if (message.type == MessageType.IMAGE) {
            lastMessageContent = "사진을 보냈습니다."
        } else {
            lastMessageContent = message.content
        }
        lastMessageSentTime = message.createdAt
    }
}
