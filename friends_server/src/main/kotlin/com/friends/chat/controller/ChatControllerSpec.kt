package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Chat")
interface ChatControllerSpec {
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
        description = "채팅방 삭제 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "채팅방 삭제 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_A_MEMBER_OF_CHAT_ROOM,
        ],
    )
    fun deleteChatRoom(
        @PathVariable
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void>

    /*@Operation(
        description = "채팅방 수정 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "채팅방 수정 성공",
            ),
        ],
    )
    fun updateChatRoom(
        @PathVariable
        chatRoomId: Long,
        @RequestPart
        @Valid
        chatRoomCreateRequestDto: ChatRoomCreateRequestDto,
        @RequestPart(required = false)
        backgroundImage: MultipartFile?,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void>*/
}
