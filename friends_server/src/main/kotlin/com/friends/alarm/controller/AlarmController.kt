package com.friends.alarm.controller

import com.friends.alarm.AlarmResponseDto
import com.friends.alarm.service.AlarmQueryService
import com.friends.common.dto.SliceBaseResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/alarm")
class AlarmController(
    private val alarmQueryService: AlarmQueryService,
) {
    @GetMapping
    fun getAlarmList(
        memberId: Long,
        size: Int,
        lastAlarmId: Long,
    ): ResponseEntity<SliceBaseResponse<AlarmResponseDto>> {
        val result = alarmQueryService.getAlarmList(memberId, size, lastAlarmId)
        return ResponseEntity.ok(result)
    }
}
