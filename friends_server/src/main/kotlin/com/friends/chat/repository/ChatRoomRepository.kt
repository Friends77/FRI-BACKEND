package com.friends.chat.repository

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun ChatRoomRepository.getByChatRoomId(chatRoomId: Long) = findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long>
