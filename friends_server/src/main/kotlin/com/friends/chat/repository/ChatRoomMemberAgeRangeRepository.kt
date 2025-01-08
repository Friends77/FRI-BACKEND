package com.friends.chat.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class ChatRoomMemberAgeRangeRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private fun getKey(chatRoomId: Long) = "chatRoom:$chatRoomId:mostAgeRange"

    fun save(
        chatRoomId: Long,
        ageRange: String,
    ) {
        val expireAt = TimeUnit.DAYS.toMillis(1) + TimeUnit.MINUTES.toMillis(30)
        redisTemplate.opsForValue().set(getKey(chatRoomId), ageRange)
        redisTemplate.expire(getKey(chatRoomId), expireAt, TimeUnit.MILLISECONDS)
    }

    fun getMostAgeRange(chatRoomId: Long): List<Int> =
        redisTemplate
            .opsForValue()
            .get(getKey(chatRoomId))
            ?.split(",")
            ?.map { it.toInt() } ?: emptyList()
}
