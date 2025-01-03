package com.friends.chat.service

import com.friends.board.repository.CategoryRepository
import com.friends.chat.ChatRoomBaseImageCannotDeleteException
import com.friends.chat.ChatRoomCategoryNotFoundException
import com.friends.chat.ChatRoomMustHaveCategoryException
import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.ChatRoomUpdateException
import com.friends.chat.NotChatRoomManagerException
import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomUpdateRequestDto
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

    @Transactional
    fun updateChatRoom(
        chatRoomId: Long,
        request: ChatRoomUpdateRequestDto?,
        memberId: Long,
        backgroundImage: MultipartFile?,
    ) {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        if (chatRoom.manager.id != memberId) throw NotChatRoomManagerException()
        val changeImageUpdate = updateChatRoomImageUrl(chatRoom, request, backgroundImage)
        val changeChatRoomInfo =
            if (request != null) {
                updateChatRoomInfo(chatRoom, request)
            } else {
                false
            }
        if (!changeImageUpdate && !changeChatRoomInfo) throw ChatRoomUpdateException()
    }

    private fun updateChatRoomImageUrl(
        chatRoom: ChatRoom,
        request: ChatRoomUpdateRequestDto?,
        backgroundImage: MultipartFile?,
    ): Boolean {
        if (backgroundImage != null) {
            if (chatRoom.imageUrl != null) {
                s3ClientService.delete(chatRoom.imageUrl!!)
            }
            chatRoom.imageUrl = s3ClientService.upload(backgroundImage)
            return true
        } else {
            if (request != null && request.backgroundImageDelete) {
                if (chatRoom.imageUrl == null) {
                    throw ChatRoomBaseImageCannotDeleteException()
                }
                s3ClientService.delete(chatRoom.imageUrl!!)
                chatRoom.imageUrl = null
                return true
            }
        }
        return false
    }

    private fun updateChatRoomInfo(
        chatRoom: ChatRoom,
        request: ChatRoomUpdateRequestDto,
    ): Boolean {
        var changeChatRoomInfo = false
        if (request.title != null && chatRoom.title != request.title) {
            chatRoom.title = request.title
            changeChatRoomInfo = true
        }
        // 채팅방의 카테고리 중 없는 카테고리 ID이면서 삭제될 카테고리 리스트에 포함되지 않은 ID를 필터링해서 카테고리 ID 리스트 가져오기
        val addCategoryList =
            request.addCategoryIds
                ?.filter { it !in chatRoom.categories.map { c -> c.category.id } && it !in request.removeCategoryIds.orEmpty() }
                ?.toSet()
                ?.let { categoryRepository.findByIdIn(it) } ?: emptySet()
        if (addCategoryList.isNotEmpty()) {
            chatRoomCategoryRepository.saveAll(
                addCategoryList
                    .map { ChatRoomCategory.of(chatRoom, it) },
            )
            changeChatRoomInfo = true
        }
        val categoriesToRemove =
            request.removeCategoryIds
                ?.let { removeCategoryIds ->
                    chatRoom.categories.filter { it.category.id in removeCategoryIds }
                }?.toSet() ?: emptySet()
        if (categoriesToRemove.isNotEmpty()) {
            chatRoomCategoryRepository.deleteAllInBatch(categoriesToRemove)
            changeChatRoomInfo = true
        }

        if (chatRoom.categories.size + addCategoryList.size - categoriesToRemove.size == 0) {
            throw ChatRoomMustHaveCategoryException()
        }

        return changeChatRoomInfo
    }
}
