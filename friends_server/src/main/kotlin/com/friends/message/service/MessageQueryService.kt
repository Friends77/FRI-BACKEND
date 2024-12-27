package com.friends.message.service

import com.friends.chat.ChatRoomMemberNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.dto.ListBaseResponse
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.dto.MessageResponseDto
import com.friends.message.dto.mapper.toMessageResponseDto
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MessageQueryService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) {
    fun getUnreadMessage(
        memberId: Long,
        chatRoomId: Long,
    ): ListBaseResponse<MessageResponseDto> {
        val member = memberRepository.findById(memberId).orElse(null) ?: throw MemberNotFoundException()
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: throw ChatRoomNotFoundException()
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: throw ChatRoomMemberNotFoundException()

        val messages = messageRepository.findUnreadMessagesForMember(chatRoomMember).map { toMessageResponseDto(it) }
        return ListBaseResponse(messages)
    }

    fun getPreviousMessages(
        chatRoomId: Long,
        messageId: Long,
        size: Int,
    ): SliceBaseResponse<MessageResponseDto> {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: throw ChatRoomNotFoundException()

        /**
         * 이전 메세지를 들을 가져와 역순으로 반환합니다.
         * 이전 메세지 조회 sql 특성상 hasNext 가 동작하기 위해서는 id 를 내림차순으로 정렬해야합니다.
         * 하지만 클라이언트에서는 message id 를 오름차순으로 정렬하여 보여주기 때문에 역순으로 반환합니다.
         */
        val messages = messageRepository.findMessagesBeforeIdInChatRoom(chatRoom, messageId, size).map { toMessageResponseDto(it) }
        messages.content.reverse()
        return toSliceBaseResponse(messages)
    }
}
