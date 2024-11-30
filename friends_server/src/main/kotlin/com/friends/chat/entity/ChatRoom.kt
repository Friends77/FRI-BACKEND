package com.friends.chat.entity

import com.friends.common.entity.BaseMongoTimeEntity
import com.friends.member.entity.Member
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
    var lastMessageId: Long = 0L,
    private val mutableParticipants: MutableList<Long> = mutableListOf(createrId),
    private val mutableMessages: MutableList<Message> = mutableListOf(),
) : BaseMongoTimeEntity() {
    val participants: List<Long>
        get() = mutableParticipants
    val messages: List<Message>
        get() = mutableMessages

    fun addParticipant(memberId: Long) {
        mutableParticipants.add(memberId)
    }

    fun removeParticipant(memberId: Long) {
        mutableParticipants.remove(memberId)
    }

    fun addMessage(message: Message) {
        mutableMessages.add(message)
        lastMessageId++
    }

    companion object {
        fun of(
            title: String,
            createrId: Long,
            categories: MutableList<String> = mutableListOf(),
            imageUrl: String? = null,
            id: String? = null,
        ): ChatRoom = ChatRoom(title = title, createrId = createrId, categories = categories, imageUrl = imageUrl, chatRoomId = id)
    }
}

class Message(
    val messageId: Long,
    val chatRoomId: String,
    val senderId: Long,
    val content: String,
    val type: MessageType,
) : BaseMongoTimeEntity() {
    companion object {
        fun of(
            messageId: Long,
            chatRoomId: String,
            senderId: Long,
            content: String,
            type: MessageType,
        ): Message = Message(messageId = messageId, chatRoomId = chatRoomId, senderId = senderId, content = content, type = type)

        fun createEnterMessage(
            member: Member,
            chatRoom: ChatRoom,
        ): Message = of(chatRoom.lastMessageId + 1, chatRoom.chatRoomId!!, 0L, "${member.name} 님이 입장하셨습니다.", MessageType.SYSTEM)
    }
}

enum class MessageType {
    TEXT, // 일반 텍스트 메시지 및 이모지
    IMAGE,
    SYSTEM, // 시스템 메시지
}
