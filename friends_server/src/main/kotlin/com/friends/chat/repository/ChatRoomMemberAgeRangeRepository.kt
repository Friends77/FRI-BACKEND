package com.friends.chat.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ChatRoomMemberAgeRangeRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private fun getKey(chatRoomId: Long) = "chatRoom:$chatRoomId:mostAgeRange"

    fun save(
        chatRoomId: Long,
        ageRange: String,
    ) = redisTemplate.opsForValue().set(getKey(chatRoomId), ageRange)

    fun getMostAgeRange(chatRoomId: Long): List<Int> =
        redisTemplate
            .opsForValue()
            .get(getKey(chatRoomId))
            ?.split(",")
            ?.map { it.toInt() } ?: emptyList()
}
