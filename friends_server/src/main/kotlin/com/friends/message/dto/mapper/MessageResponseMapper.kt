package com.friends.message.dto.mapper

import com.friends.common.util.LocalDateTimeUtil
import com.friends.message.dto.MessageResponseDto
import com.friends.message.entity.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MessageResponseMapper(
    @Value("\${image.profile-base-url}")
    private val profileBaseImageUrl: String,
) {
    fun toMessageResponseDto(
        message: Message,
    ): MessageResponseDto =
        MessageResponseDto(
            messageId = message.id,
            senderId = message.sender.id,
            profileImageUrl = message.sender.profile?.imageUrl ?: profileBaseImageUrl,
            nickname = message.sender.nickname,
            content = message.content,
            type = message.type,
            createdAt = LocalDateTimeUtil.toTimeStamp(message.createdAt),
        )
}
