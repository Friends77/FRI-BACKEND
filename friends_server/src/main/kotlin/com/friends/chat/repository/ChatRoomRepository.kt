package com.friends.chat.repository

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.entity.ChatRoom
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

fun ChatRoomRepository.getById(chatRoomId: String) = findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }

@Repository
interface ChatRoomRepository : MongoRepository<ChatRoom, String>
