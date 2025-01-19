package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.ChatRoomUpdateRequestDto
import com.friends.chat.dto.CreateChatRoomResponseDto
import com.friends.common.annotation.NullOrNotBlank
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
import org.springframework.web.bind.annotation.PathVariable
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
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = CreateChatRoomResponseDto::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.CHAT_ROOM_TITLE_BLANK,
            ErrorCode.CHAT_ROOM_TITLE_INVALID_LENGTH,
            ErrorCode.CHAT_ROOM_CATEGORY_INVALID_SIZE,
            ErrorCode.CHAT_ROOM_CATEGORY_NOT_FOUND,
            ErrorCode.NOT_FOUND_MEMBER,
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
    ): ResponseEntity<CreateChatRoomResponseDto>

    @Operation(
        description = "채팅방 리스트 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 조회 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_LAST_CHAT_ROOM_ID,
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.INVALID_SEARCH_NICKNAME,
        ],
    )
    fun getChatRooms(
        @AuthenticationPrincipal
        memberId: Long,
        @Schema(description = "해당 필드 null로 보내주시면 전체 검색, 특정 단어를 보내면 해당 단어를 포함하고 있는 닉네임 가진 유저와 함께 참여중인 채팅방을 검색합니다.")
        @RequestParam("nickname", required = false)
        @NullOrNotBlank(message = "검색할 닉네임은 공백일 수 없습니다.")
        nickname: String?,
    ): ResponseEntity<List<ChatRoomInfoResponseDto>>

    @Operation(
        description = "채팅방 상세조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "채팅방 상세 조회 성공",
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
            ErrorCode.INVALID_CHAT_ROOM_ID,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.NOT_FOUND_MEMBER,
        ],
    )
    fun getChatRoomDetail(
        @PathVariable("id")
        @Positive(message = "채팅방 ID는 양수여야 합니다.")
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<ChatRoomDetailResponseDto>

    @Operation(
        description = "채팅방 입장 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "채팅방 입장 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.INVALID_CHAT_ROOM_ID,
            ErrorCode.NOT_FOUND_MEMBER,
        ],
    )
    fun enterChatRoom(
        @PathVariable
        @Positive(message = "chatRoomId는 0보다 커야 합니다.")
        chatRoomId: Long,
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
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.INVALID_CHAT_ROOM_ID,
            ErrorCode.NOT_FOUND_MEMBER,
        ],
    )
    fun leaveChatRoom(
        @PathVariable
        @Positive(message = "chatRoomId는 0보다 커야 합니다.")
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void>

    @Operation(
        description = "채팅방 수정 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "채팅방 수정 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.INVALID_CHAT_ROOM_ID,
            ErrorCode.CHAT_ROOM_TITLE_BLANK,
            ErrorCode.INVALID_CHAT_ROOM_ID,
            ErrorCode.CHAT_ROOM_TITLE_INVALID_LENGTH,
            ErrorCode.CHAT_ROOM_CATEGORY_INVALID_SIZE,
            ErrorCode.NOT_CHAT_ROOM_MANAGER,
            ErrorCode.CHAT_ROOM_UPDATE_NOTHING,
            ErrorCode.CHAT_ROOM_BASE_IMAGE_CANNOT_DELETE,
        ],
    )
    fun updateChatRoom(
        @PathVariable("id")
        @Positive(message = "채팅방 ID는 양수여야 합니다.")
        chatRoomId: Long,
        @RequestPart(required = false)
        @Valid
        chatRoomUpdateRequestDto: ChatRoomUpdateRequestDto?,
        @RequestPart(required = false)
        backgroundImage: MultipartFile?,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void>

    @Operation(
        description = "채팅방 강제 퇴장 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "채팅방 강제 퇴장 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NOT_FORCE_LEAVE_YOURSELF,
            ErrorCode.NOT_CHAT_ROOM_MANAGER,
            ErrorCode.CHAT_ROOM_NOT_FOUND,
            ErrorCode.NOT_FOUND_MEMBER,
            ErrorCode.INVALID_CHAT_ROOM_ID,
            ErrorCode.CHAT_ROOM_MEMBER_NOT_FOUND,
        ],
    )
    fun forcedToLeave(
        @PathVariable
        @Positive(message = "chatRoomId는 0보다 커야 합니다.")
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestParam
        forceLeaveMemberId: Long,
    ): ResponseEntity<Void>
}
