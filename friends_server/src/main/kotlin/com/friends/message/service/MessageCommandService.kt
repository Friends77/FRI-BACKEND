package com.friends.message.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatSendMessageDto
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageCommandService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) {
    /**
     * 채팅방을 나갈 때마다 마지막으로 읽은 메세지 ID를 업데이트합니다.
     * 채팅방이 없거나 멤버가 없거나 채팅방 멤버가 아닐 경우 무시합니다.
     */
    @Transactional
    fun disconnectChatRoom(
        chatRoomId: Long,
        memberId: Long,
    ) {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: return // 채팅방이 없을 경우 무시
        val member = memberRepository.findById(memberId).orElse(null) ?: return // 멤버가 없을 경우 무시
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: return // 채팅방 멤버가 아닐 경우 무시

        // 마지막으로 읽은 메세지 ID 업데이트 (채팅방에 메세지가 없다면 무시)
        messageRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)?.let {
            chatRoomMember.lastReadMessage = it
        }
    }

    /**
     * 채팅방에 메세지를 보낼 메세지를 저장합니다.
     * 채팅방이 없거나 멤버가 없을 경우 예외를 발생시킵니다.
     */
    @Transactional
    fun saveMessage(
        chatRoomId: Long,
        memberId: Long,
        message: String,
        type: MessageType,
    ): ChatSendMessageDto {
        /**
         * 메세지를 보낼 때마다 보낸 유저와 채팅방이 있는지 DB 에 확인합니다.
         * 이 과정이 비효율적일 경우 아래 프록시 객체를 생성하여 메세지를 보내는 로직을 고려합니다.
         * 프록시 객체는 DB 에서 채팅방과 유저 정보를 요청하지 않지만, DB 에 데이터가 있는지 없는지 확인할 수 없습니다.
         *
         * val chatRoom = entityManager.getReference(ChatRoom::class.java, chatRoomId)
         * val sender = entityManager.getReference(Member::class.java, message.senderId)
         */
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
        val sender = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }

        val savedMessage = messageRepository.save(Message.of(chatRoom, sender, message, type))
        return ChatSendMessageDto(
            chatRoomId = chatRoomId,
            senderId = sender.id,
            message = savedMessage.content,
            sendTime = savedMessage.createdAt,
            type = savedMessage.type,
        )
    }
}
