package com.friends.chat.dto.category

import io.swagger.v3.oas.annotations.media.Schema

data class ChatSubjectInfoResponse(
    @Schema(description = "채팅방 주제 카테고리 ID")
    val id: Long,
    @Schema(description = "채팅방 주제 카테고리 이름")
    val name: String,
)
