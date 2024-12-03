package com.friends.chat.entity

import com.friends.chat.PositiveLikeCountException
import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "chat_room")
class ChatRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    val id: Long = 0L,
    @Column(nullable = false)
    var title: String,
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    var manager: Member,
    var imageUrl: String?, // 채팅방 이미지
    @Column(nullable = false)
    var likeCount: Int = 0,
    @Column(nullable = false)
    @OneToMany(mappedBy = "chatRoom")
    var categories: MutableList<ChatRoomCategory> = mutableListOf(),
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(nullable = true)
    var lastMessage: Message? = null,
) : BaseModifiableEntity() {
    fun increaseLikeCount() {
        this.likeCount++
    }

    fun decreaseLikeCount() {
        require(this.likeCount > 0) { throw PositiveLikeCountException() }
        this.likeCount--
    }

    fun changeManager(manager: Member) {
        this.manager = manager
    }

    fun changeLastMessage(message: Message) {
        this.lastMessage = message
    }

    companion object {
        fun of(
            title: String,
            manager: Member,
            imageUrl: String? = null,
        ): ChatRoom = ChatRoom(title = title, manager = manager, imageUrl = imageUrl)
    }
}
