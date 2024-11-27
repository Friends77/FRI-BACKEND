package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    fun createChatRoom(
        @RequestPart
        @Valid
        chatRoomRequest: ChatRoomRequest,
        @RequestPart
        backgroundImage: MultipartFile?,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void>
}
