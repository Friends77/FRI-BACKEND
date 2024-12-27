package com.friends.message.service

import com.friends.chat.ChatRoomMemberNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.dto.SliceBaseResponse
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service

@Service
class MessageQueryService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) {
    fun getUnreadMessage(
        memberId: Long,
        chatRoomId: Long,
    ): SliceBaseResponse<MessageListDto> {
        val member = memberRepository.findById(memberId).orElse(null) ?: throw MemberNotFoundException()
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: throw ChatRoomNotFoundException()
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: throw ChatRoomMemberNotFoundException()

        return messageRepository.getUnreadMessages(chatRoomMember)
    }
}
