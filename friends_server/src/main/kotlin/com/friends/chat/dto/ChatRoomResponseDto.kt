package com.friends.chat.dto

import com.friends.chat.dto.category.CategoryInfoResponse
import io.swagger.v3.oas.annotations.media.Schema

data class ChatRoomInfoResponseDto(
    @Schema(description = "채팅방 ID")
    val id: Long,
    @Schema(description = "채팅방 제목")
    val title: String,
    @Schema(description = "채팅방 이미지 URL")
    val imageUrl: String?,
    @Schema(description = "채팅방 카테고리 리스트")
    val categoryIdList: List<CategoryInfoResponse>,
    @Schema(description = "채팅방 참여자 수")
    val participantCount: Int,
    @Schema(description = "채팅방 좋아요 수")
    val likeCount: Int,
)
