package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.util.SliceChatRoomInfoResponseDto
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Chat")
interface ChatRoomControllerSpec {
    @Operation(
        description = "채팅방 생성 API",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "채팅방 생성 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.CHAT_ROOM_TITLE_BLANK,
            ErrorCode.CHAT_ROOM_TITLE_INVALID_LENGTH,
            ErrorCode.CHAT_ROOM_CATEGORY_INVALID_SIZE,
            ErrorCode.CHAT_ROOM_CATEGORY_NOT_FOUND,
        ],
    )
    fun createChatRoom(
        @RequestPart
        @Valid
        chatRoomCreateRequestDto: ChatRoomCreateRequestDto,
        @RequestPart(required = false)
        backgroundImage: MultipartFile?,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void>

    @Operation(
        description = "채팅방 리스트 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = SliceChatRoomInfoResponseDto::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_SIZE,
            ErrorCode.INVALID_LAST_CHAT_ROOM_ID,
        ],
    )
    fun getChatRooms(
        @AuthenticationPrincipal
        memberId: Long,
        @Positive(message = "size는 양수여야 합니다.")
        @RequestParam("size", defaultValue = "100")
        size: Int,
        @Positive(message = "lastChatRoomId는 양수여야 합니다.")
        @RequestParam("lastChatRoomId", required = false)
        lastChatRoomId: Long?,
        @Schema(description = "친구 닉네임 기반 친구와 함께 참여 중인 채팅방 리스트는 아직 구현되지 않았습니다, 해당 필드 null로 보내주시면 전체 검색됩니다.")
        @RequestParam("nickname", required = false)
        nickname: String?,
    ): ResponseEntity<SliceBaseResponse<ChatRoomInfoResponseDto>>
}
