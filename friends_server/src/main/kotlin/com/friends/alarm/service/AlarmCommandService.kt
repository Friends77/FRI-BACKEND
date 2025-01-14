package com.friends.alarm.service

import com.friends.alarm.entity.Alarm
import com.friends.alarm.entity.AlarmType
import com.friends.alarm.repository.AlarmRepository
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AlarmCommandService(
    private val memberRepository: MemberRepository,
    private val alarmRepository: AlarmRepository,
    private val chatRoomRepository: ChatRoomRepository,
) {
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
        // TODO 웹소켓을 이용하여 알람 전송
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
        // TODO 웹소켓을 이용하여 알람 전송
    }

    private fun sendAlarm(
        alarm: Alarm,
    ) {
        alarmRepository.save(alarm)
        // TODO 웹소켓을 이용하여 알람 전송
    }
}
