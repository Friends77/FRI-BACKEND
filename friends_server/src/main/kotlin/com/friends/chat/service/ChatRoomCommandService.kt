package com.friends.chat.service

import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.entity.ChatRoomMember
import com.friends.chat.entity.Message
import com.friends.chat.repository.ChatRoomCategoryRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.chat.repository.ChatSubjectCategoryRepository
import com.friends.chat.repository.MessageRepository
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
    private val chatSubjectCategoryRepository: ChatSubjectCategoryRepository,
    private val chatRoomCategoryRepository: ChatRoomCategoryRepository,
    private val messageRepository: MessageRepository,
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
        val chatRoom = chatRoomRepository.save(ChatRoom.of(request.title, member, imageUrl))
        chatRoomCategoryRepository.saveAll(chatSubjectCategoryRepository.findByIdIn(request.categoryIdList).also { if (it.isEmpty()) throw ChatRoomCategoryNotFoundException() }.map { ChatRoomCategory(chatRoom, it) })
        chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom, member))
        messageRepository.save(Message.createEnterMessage(member, chatRoom))
    }
}
