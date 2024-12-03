package com.friends.chat.entity

import com.friends.common.entity.BaseTimeEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    val chatRoom: ChatRoom,
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    val sender: Member,
    @Column(nullable = false)
    val content: String,
    @Column(nullable = false)
    val type: MessageType,
) : BaseTimeEntity() {
    companion object {
        fun of(
            chatRoom: ChatRoom,
            sender: Member,
            content: String,
            type: MessageType,
        ): Message = Message(0L, chatRoom, sender, content, type)
    }

    fun createEnterMessage(
        sender: Member,
        chatRoom: ChatRoom,
    ): Message = of(chatRoom, sender, "${sender.name} 님이 입장하셨습니다.", MessageType.SYSTEM)
}

enum class MessageType {
    TEXT, // 일반 텍스트 메시지 및 이모지
    IMAGE,
    SYSTEM,
}
