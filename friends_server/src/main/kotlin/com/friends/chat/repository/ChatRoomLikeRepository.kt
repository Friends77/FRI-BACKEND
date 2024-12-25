package com.friends.chat.repository

import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomLike
import com.friends.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomLikeRepository : JpaRepository<ChatRoomLike, Long> {
    fun existsByChatRoomAndMember(
        chatRoom: ChatRoom,
        member: Member,
    ): Boolean

    fun deleteByChatRoomAndMember(
        chatRoom: ChatRoom,
        member: Member,
    )

    fun countByChatRoom(
        chatRoom: ChatRoom,
    ): Int
}
