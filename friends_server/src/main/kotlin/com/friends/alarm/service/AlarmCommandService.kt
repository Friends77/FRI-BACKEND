package com.friends.alarm.service

import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.AlarmType
import com.friends.alarm.repository.AlarmRepository
import com.friends.alarm.toAlarmResponseDto
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.util.JsonUtil
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Service
@Transactional
class AlarmCommandService(
    private val memberRepository: MemberRepository,
    private val alarmRepository: AlarmRepository,
    private val chatRoomRepository: ChatRoomRepository,
) {
    private val onlineUserSessions = ConcurrentHashMap<Long, MutableSet<WebSocketSession>>()

    fun addOnlineUserSession(
        memberId: Long,
        session: WebSocketSession,
    ) {
        onlineUserSessions.computeIfAbsent(memberId) { ConcurrentHashMap.newKeySet() }.add(session)
    }

    fun removeOnlineUserSession(
        memberId: Long,
        session: WebSocketSession,
    ) {
        onlineUserSessions[memberId]?.removeIf {
            it.id == session.id
        }
    }

    fun sendFriendRequestAlarm(
        requesterId: Long,
        receiverId: Long,
    ) {
        val requester = memberRepository.findById(requesterId).orElseThrow { MemberNotFoundException() }
        val receiver = memberRepository.findById(receiverId).orElseThrow { MemberNotFoundException() }
        val alarm =
            Alarm(
                sender = requester,
                receiver = receiver,
                type = AlarmType.FRIEND_REQUEST,
                message = "${requester.nickname}님이 친구 요청을 보냈습니다.",
            )

        sendAlarm(alarm)
    }

    fun sendChatInvitationAlarm(
        senderId: Long,
        receiverId: Long,
        chatRoomId: Long,
    ) {
        val sender = memberRepository.findById(senderId).orElseThrow { MemberNotFoundException() }
        val receiver = memberRepository.findById(receiverId).orElseThrow { MemberNotFoundException() }
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
        val alarm =
            Alarm(
                sender = sender,
                receiver = receiver,
                type = AlarmType.CHAT_ROOM_INVITATION,
                message = "${sender.nickname}님이 채팅방[${chatRoom.title}]에 초대를 보냈습니다.",
                invitedChatRoom = chatRoom,
            )

        sendAlarm(alarm)
    }

    private fun sendAlarm(
        alarm: Alarm,
    ) {
        alarmRepository.save(alarm)
        val alarmResponseDto = toAlarmResponseDto(alarm)
        onlineUserSessions[alarm.receiver.id]?.forEach {
            it.sendMessage(TextMessage(JsonUtil.toJson(alarmResponseDto)))
        }
    }
}
