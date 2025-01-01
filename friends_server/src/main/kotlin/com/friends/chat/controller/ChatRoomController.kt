package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.dto.CreateChatRoomResponseDto
import com.friends.chat.service.ChatRoomCommandService
import com.friends.chat.service.ChatRoomQueryService
import com.friends.common.dto.SliceBaseResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/user/chat/room")
class ChatRoomController(
    private val chatRoomCommandService: ChatRoomCommandService,
    private val chatRoomQueryService: ChatRoomQueryService,
) : ChatRoomControllerSpec {
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun createChatRoom(
        @RequestPart
        @Valid
        chatRoomCreateRequestDto: ChatRoomCreateRequestDto,
        @RequestPart(required = false)
        backgroundImage: MultipartFile?,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<CreateChatRoomResponseDto> = ResponseEntity.status(HttpStatus.CREATED).body(chatRoomCommandService.createChatRoom(chatRoomCreateRequestDto, memberId, backgroundImage))

    @GetMapping
    override fun getChatRooms(
        @AuthenticationPrincipal
        memberId: Long,
        @Positive(message = "size는 양수여야 합니다.")
        @RequestParam("size", defaultValue = "100")
        size: Int,
        @Positive(message = "lastChatRoomId는 양수여야 합니다.")
        @RequestParam("lastChatRoomMemberId", required = false)
        lastChatRoomMemberId: Long?,
        @RequestParam("nickname", required = false)
        nickname: String?,
    ): ResponseEntity<SliceBaseResponse<ChatRoomInfoResponseDto>> = ResponseEntity.ok(chatRoomQueryService.getChatRooms(memberId, size, lastChatRoomMemberId, nickname))

    @GetMapping("/{id}")
    override fun getChatRoomDetail(
        @PathVariable("id")
        @Positive(message = "채팅방 ID는 양수여야 합니다.")
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<ChatRoomDetailResponseDto> = ResponseEntity.ok(chatRoomQueryService.getChatRoomDetail(chatRoomId, memberId))

    @PostMapping("/{chatRoomId}")
    override fun enterChatRoom(
        @PathVariable
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void> {
        chatRoomCommandService.enterChatRoom(chatRoomId, memberId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @DeleteMapping("/{chatRoomId}")
    override fun leaveChatRoom(
        @PathVariable
        chatRoomId: Long,
        @AuthenticationPrincipal
        memberId: Long,
    ): ResponseEntity<Void> {
        chatRoomCommandService.leaveChatRoom(chatRoomId, memberId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
