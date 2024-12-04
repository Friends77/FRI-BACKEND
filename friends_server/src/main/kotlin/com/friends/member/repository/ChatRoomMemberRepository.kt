package com.friends.member.repository

import com.friends.chat.entity.ChatRoomMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomMemberRepository : JpaRepository<ChatRoomMember, Long>
