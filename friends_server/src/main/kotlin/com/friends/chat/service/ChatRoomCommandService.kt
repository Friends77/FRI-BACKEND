package com.friends.chat.service

import com.friends.category.repository.CategoryRepository
import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.CreateChatRoomResponseDto
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomCategory
import com.friends.chat.entity.ChatRoomMember
import com.friends.chat.repository.ChatRoomCategoryRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.chat.websocket.ChatWebSocketHandler
import com.friends.image.S3ClientService
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ChatRoomCommandService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val chatRoomCategoryRepository: ChatRoomCategoryRepository,
    private val messageRepository: MessageRepository,
    private val s3ClientService: S3ClientService,
    private val chatWebSocketHandler: ChatWebSocketHandler,
) {
    @Transactional
    fun createChatRoom(
        request: ChatRoomCreateRequestDto,
        memberId: Long,
        backgroundImage: MultipartFile?,
    ): CreateChatRoomResponseDto {
        val imageUrl =
            backgroundImage?.let {
                s3ClientService.upload(it)
            }
        val member = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        val chatRoom = chatRoomRepository.save(ChatRoom.of(request.title, member, imageUrl))
        chatRoomCategoryRepository.saveAll(categoryRepository.findByIdIn(request.categoryIdList).also { if (it.isEmpty()) throw ChatRoomCategoryNotFoundException() }.map { ChatRoomCategory.of(chatRoom, it) })
        val enterMassage = messageRepository.save(Message.createEnterMessage(member, chatRoom))
        chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom, member, enterMassage))
        return CreateChatRoomResponseDto(chatRoom.id)
    }

    @Transactional
    fun enterChatRoom(
        chatRoomId: Long,
        memberId: Long,
    ) {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
        val member = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        if (!chatRoomMemberRepository.existsChatRoomMemberByChatRoomAndMember(chatRoom, member)) {
            val enterMessage = messageRepository.save(Message.createEnterMessage(member, chatRoom))
            chatRoomMemberRepository.save(ChatRoomMember.of(chatRoom, member, enterMessage))
            chatWebSocketHandler.sendMessage(chatRoomId, enterMessage)
        }
    }
}
