package com.friends.chat.dto

import com.friends.category.dto.CategoryInfoResponse
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class ChatRoomInfoResponseDto(
    @Schema(description = "참여하는 채팅방 연관 ID")
    val chatRoomMemberId: Long,
    @Schema(description = "채팅방 ID")
    val id: Long,
    @Schema(description = "채팅방 제목")
    val title: String,
    @Schema(description = "채팅방 이미지 URL, 없을 경우 null")
    val imageUrl: String?,
    @Schema(description = "채팅방 카테고리 리스트")
    val categoryIdList: List<CategoryInfoResponse>,
    @Schema(description = "채팅방 참여자 수")
    val participantCount: Int,
    @Schema(description = "채팅방 마지막 메세지 시간")
    val lastMessageTime: LocalDateTime,
    @Schema(description = "안 읽은 메세지 수")
    val unreadMessageCount: Int,
)
