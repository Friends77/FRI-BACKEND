package com.friends.alarm.service

import com.friends.alarm.AlarmResponseDto
import com.friends.alarm.repository.AlarmRepository
import com.friends.alarm.toAlarmResponseDto
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.mapper.toSliceBaseResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AlarmQueryService(
    private val alarmRepository: AlarmRepository,
) {
    fun getAlarmList(
        memberId: Long,
        size: Int,
        lastAlarmId: Long?,
    ): SliceBaseResponse<AlarmResponseDto> {
        val result =
            alarmRepository.findAllByMemberIdBeforeId(memberId, size, lastAlarmId).map {
                toAlarmResponseDto(it)
            }
        return toSliceBaseResponse(result)
    }
}
