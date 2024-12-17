package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity

@Tag(name = "Chat")
interface ChatRoomGlobalControllerSpec {
    @Operation(
        description = "채팅방 정보 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChatRoomDetailResponseDto::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.INVALID_CHAT_ROOM_ID,
        ],
    )
    fun getChatRoomDetail(
        @Positive
        id: Long,
    ): ResponseEntity<ChatRoomDetailResponseDto>
}
