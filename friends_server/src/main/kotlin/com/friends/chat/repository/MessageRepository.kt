package com.friends.chat.repository

import com.friends.chat.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
    fun deleteByChatRoomId(chatRoomId: Long)
}
