package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.ChatRoomUpdateRequestDto
import com.friends.chat.dto.CreateChatRoomResponseDto
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
            ErrorCode.NOT_FOUND_MEMBER,
        ],
    )
    fun getChatRooms(
        @AuthenticationPrincipal
        memberId: Long,
        @Positive(message = "size는 양수여야 합니다.")
        @RequestParam("size", defaultValue = "100")
        size: Int,
        @Positive(message = "lastChatRoomId는 양수여야 합니다.")
        @RequestParam("lastChatRoomMemberId", required = false)
        @Schema(description = "마지막으로 조회된 참여하는 채팅방 연관 ID를 넣어주면 됩니다. 처음부터 조회시 null로 보내주시면 됩니다.")
        lastChatRoomMemberId: Long?,
        @Schema(description = "친구 닉네임 기반 친구와 함께 참여 중인 채팅방 리스트는 아직 구현되지 않았습니다, 해당 필드 null로 보내주시면 전체 검색됩니다.")
        @RequestParam("nickname", required = false)
        nickname: String?,
    ): ResponseEntity<SliceBaseResponse<ChatRoomInfoResponseDto>>

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
