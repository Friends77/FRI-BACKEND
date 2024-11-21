package com.friends.member.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class ChatRoomMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_member_id")
    val id: Long = 0L,
    @Column(name = "chat_room_id", updatable = false, nullable = false)
    val chatRoomId: String,
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
    @Column(name = "last_read_message_id")
    val lastReadMessageId: String? = null,
) {
    companion object {
        fun of(
            chatRoomId: String,
            member: Member,
            lastReadMessageId: String? = null,
        ): ChatRoomMember =
            ChatRoomMember(
                chatRoomId = chatRoomId,
                member = member,
                lastReadMessageId = lastReadMessageId,
            )
    }
}
