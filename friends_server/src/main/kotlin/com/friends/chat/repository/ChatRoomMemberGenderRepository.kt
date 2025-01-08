package com.friends.chat.repository

import com.friends.profile.entity.GenderEnum
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class ChatRoomMemberGenderRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private fun getKey(chatRoomId: Long) = "chatRoom:$chatRoomId:mostGender"

    fun save(
        chatRoomId: Long,
        genderRatio: String,
    ) {
        val expireAt = TimeUnit.DAYS.toMillis(1) + TimeUnit.MINUTES.toMillis(30)
        redisTemplate.opsForValue().set(getKey(chatRoomId), genderRatio)
        redisTemplate.expire(getKey(chatRoomId), expireAt, TimeUnit.MILLISECONDS)
    }

    fun getMostGender(chatRoomId: Long): List<GenderEnum> =
        redisTemplate
            .opsForValue()
            .get(getKey(chatRoomId))
            ?.split(",")
            ?.map { GenderEnum.valueOf(it) } ?: emptyList()
}
