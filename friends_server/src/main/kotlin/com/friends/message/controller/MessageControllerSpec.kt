package com.friends.message.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import com.friends.message.dto.MessageResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Message")
interface MessageControllerSpec {
    @Operation(
        description =
            "읽지 않은 메세지 조회 API <br>" +
                "로그인 된 사용자만 사용 가능합니다",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "읽지 않은 메세지 조회 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.CHAT_ROOM_MEMBER_NOT_FOUND,
        ],
    )
    fun getUnreadMessages(
        memberId: Long,
        chatRoomId: Long,
    ): ResponseEntity<ListBaseResponse<MessageResponseDto>>

    @Operation(
        description =
            "이전 메세지 조회 API <br>" +
                "로그인 된 사용자만 사용 가능합니다",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "이전 메세지 조회 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.CHAT_ROOM_MEMBER_NOT_FOUND,
        ],
    )
    fun getPreviousMessage(
        memberId: Long,
        chatRoomId: Long,
        size: Int,
        lastMessageId: Long?,
    ): ResponseEntity<SliceBaseResponse<MessageResponseDto>>

    @Operation(
        description =
            "이미지 업로드 API <br>" +
                "로그인 된 사용자만 사용 가능합니다",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "이미지 업로드 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.INVALID_CHAT_ROOM_ID,
        ],
    )
    fun uploadImage(
        @AuthenticationPrincipal
        memberId: Long,
        @PathVariable("chatRoomId")
        @Positive(message = "채팅방 ID는 양수여야 합니다.")
        chatRoomId: Long,
        @RequestPart
        image: MultipartFile,
    ): ResponseEntity<Void>
}
