package com.friends.chat.repository

import com.friends.chat.NotChatRoomMemberException
import com.friends.chat.entity.ChatRoomMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun ChatRoomMemberRepository.getByMemberIdAndChatRoomId(
    memberId: Long,
    chatRoomId: Long,
): ChatRoomMember = findChatRoomByMemberIdAndChatRoomId(memberId, chatRoomId) ?: throw NotChatRoomMemberException()

@Repository
interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long> {
    fun existsByMemberIdAndChatRoomId(
        memberId: Long,
        chatRoomId: Long,
    ): Boolean

    fun findChatRoomByMemberIdAndChatRoomId(
        memberId: Long,
        chatRoomId: Long,
    ): ChatRoomMember?

    fun countByChatRoomId(chatRoomId: Long): Int
}
