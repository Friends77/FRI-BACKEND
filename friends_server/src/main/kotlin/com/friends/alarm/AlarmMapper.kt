package com.friends.alarm

import com.friends.alarm.entity.Alarm
import com.friends.common.util.LocalDateTimeUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AlarmMapper(
    @Value("\${image.profile-base-url}")
    private val profileBaseImageUrl: String,
) {
    fun toAlarmResponseDto(alarm: Alarm) =
        AlarmResponseDto(
            id = alarm.id,
            type = alarm.getType(),
            message = alarm.message,
            senderId = alarm.sender.id,
            senderProfileImage = alarm.sender.profile?.imageUrl ?: profileBaseImageUrl,
            nickname = alarm.sender.nickname,
            receiverId = alarm.receiver.id,
            invitedChatRoomId = alarm.invitedChatRoom?.id,
            createdAt = LocalDateTimeUtil.toTimeStamp(alarm.createdAt),
        )
}
