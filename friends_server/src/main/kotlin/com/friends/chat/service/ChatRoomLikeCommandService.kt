package com.friends.chat.service

import com.friends.chat.dto.ToggleLikeResponseDto
import com.friends.chat.entity.ChatRoomLike
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.chat.repository.getByChatRoomId
import com.friends.member.repository.MemberRepository
import com.friends.member.repository.getByMemberId
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
        val chatRoom = chatRoomRepository.getByChatRoomId(chatRoomId)
        val member = memberRepository.getByMemberId(memberId)
        val liked =
            if (chatRoomLikeRepository.existsByChatRoomAndMember(chatRoom, member)) {
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
