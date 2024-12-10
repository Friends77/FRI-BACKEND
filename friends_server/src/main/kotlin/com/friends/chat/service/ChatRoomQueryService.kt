package com.friends.chat.service

import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.mapper.toChatRoomInfoResponse
import com.friends.chat.entity.ChatRoom
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.member.entity.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomQueryService(
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) {
    @Transactional
    fun getChatRooms(
        memberId: Long,
        size: Int,
        lastChatRoomId: Long?,
        nickname: String?,
    ): SliceBaseResponse<ChatRoomInfoResponseDto> {
        var chatRooms: List<ChatRoom> = listOf()
        if (nickname != null) {
            val friends: List<Member> = listOf() // TODO: 닉네임으로 친구 조회(Like 검색?)
            if (friends.isNotEmpty()) {
                chatRooms = chatRoomMemberRepository.findChatRoomByMemberListIn(friends)
            }
        }
        val chatRoomInfoResponse = chatRoomMemberRepository.sliceChatRoomIdByMember(memberId, chatRooms, size, lastChatRoomId).map { toChatRoomInfoResponse(it, chatRoomMemberRepository.countByChatRoom(it)) }
        return toSliceBaseResponse(chatRoomInfoResponse)
    }
}
