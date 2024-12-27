package com.friends.chat.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.mapper.toChatRoomDetailResponseDto
import com.friends.chat.dto.mapper.toChatRoomInfoResponse
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.mapper.toSliceBaseResponse
import com.friends.member.entity.Member
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomQueryService(
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomLikeRepository: ChatRoomLikeRepository,
) {
    @Transactional
    fun getChatRooms(
        memberId: Long,
        size: Int,
        lastChatRoomMemberId: Long?,
        nickname: String?,
    ): SliceBaseResponse<ChatRoomInfoResponseDto> {
        val friends: List<Member> =
            if (nickname != null) {
                listOf() // TODO: 닉네임으로 친구 조회(Like 검색?)
            } else {
                listOf()
            }
        val chatRoomInfoResponse =
            chatRoomMemberRepository
                .sliceChatRoomIdByMember(memberId, friends, size, lastChatRoomMemberId)
                .map {
                    toChatRoomInfoResponse(
                        it,
                        chatRoomMemberRepository.countByChatRoom(it.chatRoom),
                        messageRepository.countUnreadMessages(it).let { count -> if (count > 999) 999 else count },
                    )
                } //해당 채팅방 멤버 수와 읽지 않은 메세지 수를 가져옴
        return toSliceBaseResponse(chatRoomInfoResponse)
    }

    @Transactional
    fun getChatRoomDetail(
        chatRoomId: Long,
        memberId: Long,
    ): ChatRoomDetailResponseDto {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        return toChatRoomDetailResponseDto(chatRoom, chatRoomMemberRepository.countByChatRoom(chatRoom), chatRoomLikeRepository.existsByChatRoomAndMemberId(chatRoom, memberId))
    }
}
