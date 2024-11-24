package com.friends.chat.entity

import com.friends.common.entity.BaseMongoTimeEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("chat_room")
class ChatRoom(
    @Id
    val chatRoomId: String? = null,
    var title: String,
    val createrId: Long,
    var imageUrl: String?, // 채팅방 이미지
    var categories: MutableList<String> = mutableListOf(),
    var lastMessageId: Long = 0,
) : BaseMongoTimeEntity() {
    private val _participants: MutableList<Long> = mutableListOf(createrId)
    val participants: List<Long>
        get() = _participants
    private val _messages: MutableList<Message> = mutableListOf()
    val messages: List<Message>
        get() = _messages

    fun addParticipant(memberId: Long) {
        _participants.add(memberId)
    }

    fun removeParticipant(memberId: Long) {
        _participants.remove(memberId)
    }

    fun addMessage(message: Message) {
        _messages.add(message)
    }
}

class Message(
    var messageId: Long,
    val chatRoomId: String,
    val senderId: Long,
    val content: String,
    val type: MessageType,
) : BaseMongoTimeEntity()
