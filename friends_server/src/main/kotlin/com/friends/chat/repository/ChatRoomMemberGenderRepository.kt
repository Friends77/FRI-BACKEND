package com.friends.chat.repository

import com.friends.profile.entity.GenderEnum
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class ChatRoomMemberGenderRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private fun getKey(chatRoomId: Long) = "chatRoom:$chatRoomId:mostGender"

    fun save(
        chatRoomId: Long,
        genderRatio: String,
    ) = redisTemplate.opsForValue().set(getKey(chatRoomId), genderRatio)

    fun getMostGender(chatRoomId: Long): List<GenderEnum> =
        redisTemplate
            .opsForValue()
            .get(getKey(chatRoomId))
            ?.split(",")
            ?.map { GenderEnum.valueOf(it) } ?: emptyList()
}
