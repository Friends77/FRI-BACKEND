package com.friends.chat.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.mapper.toChatRoomDetailResponseDto
import com.friends.chat.dto.mapper.toChatRoomInfoResponse
import com.friends.chat.repository.ChatRoomLikeRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.friendship.repository.FriendshipRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.message.repository.MessageRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomQueryService(
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomLikeRepository: ChatRoomLikeRepository,
    private val memberRepository: MemberRepository,
    private val friendshipRepository: FriendshipRepository,
    @Value("\${image.chat-room-base-url}")
    private val chatRoomBaseImageUrl: String,
    @Value("\${image.profile-base-url}")
    private val profileBaseImageUrl: String,
) {
    @Transactional
    fun getChatRooms(
        memberId: Long,
        nickname: String?,
    ): List<ChatRoomInfoResponseDto> {
        memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        val friends: List<Member>? =
            if (nickname != null) {
                friendshipRepository.findFriendshipByMemberIdAndNickname(memberId, nickname).also { if (it.isEmpty()) return emptyList() }
            } else {
                null
            }
        val chatRoomInfoResponse =
            chatRoomMemberRepository
                .findAllByMemberAndFriends(memberId, friends)
                .map {
                    val lastMessage = messageRepository.findRecentMessageInChatRoom(it.chatRoom)
                    toChatRoomInfoResponse(
                        it,
                        chatRoomMemberRepository.countByChatRoom(it.chatRoom),
                        chatRoomMemberRepository.findRepresentativeProfileByChatRoomId(it.chatRoom.id).map { member -> member.profile?.imageUrl ?: profileBaseImageUrl },
                        messageRepository.countUnreadMessages(it).let { count -> if (count > 999) 999 else count },
                        lastMessage,
                        it.chatRoom.imageUrl ?: chatRoomBaseImageUrl,
                    )
                } //해당 채팅방 멤버 수와 읽지 않은 메세지 수를 가져옴
        return chatRoomInfoResponse
    }

    @Transactional(readOnly = true)
    fun getChatRoomDetail(
        chatRoomId: Long,
        memberId: Long,
    ): ChatRoomDetailResponseDto {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { throw ChatRoomNotFoundException() }
        memberRepository.findById(memberId).orElseThrow { throw MemberNotFoundException() }
        return toChatRoomDetailResponseDto(chatRoom, chatRoomMemberRepository.countByChatRoom(chatRoom), chatRoomLikeRepository.existsByChatRoomAndMemberId(chatRoom, memberId), chatRoom.imageUrl ?: chatRoomBaseImageUrl)
    }
}
