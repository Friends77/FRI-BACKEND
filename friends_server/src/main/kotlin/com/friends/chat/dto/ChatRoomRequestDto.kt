package com.friends.chat.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

data class ChatRoomCreateRequestDto(
    @Schema(description = "채팅방 제목")
    @field:NotBlank(message = "채팅방 제목은 공백일 수 없습니다.")
    @field:Length(min = 1, max = 20, message = "채팅방 제목은 1자 이상 20자 이하로 입력해주세요.") //TODO: 제목 최대 길이 제한
    val title: String,
    @field:Size(min = 1, message = "채팅방 카테고리는 최소 1개 이상 선택해주세요.") //TODO: 카테고리 최대 개수 제한
    @Schema(description = "채팅방 카테고리 ID 리스트", required = false)
    val categoryIdList: Set<Long>,
)
