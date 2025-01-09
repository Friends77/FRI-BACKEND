package com.friends.alarm

import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.AlarmType
import java.time.LocalDateTime

data class AlarmResponseDto(
    val id: Long,
    val type: AlarmType,
    val message: String,
    val createdAt: LocalDateTime,
)

fun toAlarmResponseDto(alarm: Alarm) =
    AlarmResponseDto(
        id = alarm.id,
        type = alarm.type,
        message = alarm.message,
        createdAt = alarm.createdAt,
    )
