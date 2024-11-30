package com.friends.chat.service

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.Message
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.entity.ChatRoomMember
import com.friends.member.repository.ChatRoomMemberRepository
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ChatRoomCommandService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun createChatRoom(
        request: ChatRoomCreateRequestDto,
        memberId: Long,
        backgroundImage: MultipartFile?,
    ) {
        val imageUrl =
            backgroundImage?.let {
                /* 이미지 업로드 로직 */ backgroundImage.name
            }
        val member = memberRepository.findById(memberId).get()
        var chatRoom = ChatRoom.of(request.title, memberId, request.categories, imageUrl)
        chatRoom.addMessage(Message.createEnterMessage(member, chatRoom))
        chatRoom = chatRoomRepository.save(ChatRoom.of(request.title, memberId, request.categories, imageUrl))
        chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom.chatRoomId!!, member))
    }
}
