package com.friends.chat.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ToggleLikeResponseDto
import com.friends.chat.entity.ChatRoomLike
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomLikeCommandService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomLikeRepository: ChatRoomLikeRepository,
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun toggleLike(
        chatRoomId: Long,
        memberId: Long,
    ): ToggleLikeResponseDto {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        val member = memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        val liked =
            if (chatRoomLikeRepository.existsByChatRoomAndMemberId(chatRoom, member.id)) {
                chatRoomLikeRepository.deleteByChatRoomAndMember(chatRoom, member)
                chatRoom.decreaseLikeCount()
                false
            } else {
                chatRoomLikeRepository.save(ChatRoomLike.of(chatRoom, member))
                chatRoom.increaseLikeCount()
                true
            }
        return ToggleLikeResponseDto(chatRoomId, chatRoom.likeCount, liked)
    }
}
