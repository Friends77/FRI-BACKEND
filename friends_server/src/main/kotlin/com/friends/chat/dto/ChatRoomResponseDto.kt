package com.friends.chat.dto

import com.friends.chat.dto.category.ChatSubjectInfoResponse

data class ChatRoomInfoResponseDto(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val categoryIdList: List<ChatSubjectInfoResponse>,
    val participantCount: Int,
    val likeCount: Int,
)
