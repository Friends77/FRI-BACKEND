package com.friends.alarm.service

import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.AlarmType
import com.friends.alarm.repository.AlarmRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AlarmCommandService(
    private val memberRepository: MemberRepository,
    private val alarmRepository: AlarmRepository,
) {
    fun sendAlarm(
        memberId: Long,
        alarmType: AlarmType,
        alarmMessage: String,
    ) {
        val member = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        alarmRepository.save(
            Alarm(
                member = member,
                type = alarmType,
                message = alarmMessage,
            ),
        )
        // TODO 웹소켓을 이용하여 알람 전송
    }
}
