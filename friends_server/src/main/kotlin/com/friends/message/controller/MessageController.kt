package com.friends.message.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.common.dto.SliceBaseResponse
import com.friends.message.dto.MessageResponseDto
import com.friends.message.service.MessageCommandService
import com.friends.message.service.MessageQueryService
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/user/message")
class MessageController(
    private val messageQueryService: MessageQueryService,
    private val messageCommandService: MessageCommandService,
) : MessageControllerSpec {
    @GetMapping("/unread/{chatRoomId}")
    override fun getUnreadMessages(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable chatRoomId: Long,
    ): ResponseEntity<ListBaseResponse<MessageResponseDto>> {
        val result = messageQueryService.getUnreadMessage(memberId, chatRoomId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/previous/{chatRoomId}")
    override fun getPreviousMessage(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable chatRoomId: Long,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("lastMessageId", required = false) lastMessageId: Long?,
    ): ResponseEntity<SliceBaseResponse<MessageResponseDto>> {
        val result = messageQueryService.getPreviousMessages(chatRoomId, memberId, lastMessageId, size)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/{chatRoomId}/image", consumes = ["multipart/form-data"])
    override fun uploadImage(
        @AuthenticationPrincipal
        memberId: Long,
        @PathVariable("chatRoomId")
        @Positive(message = "채팅방 ID는 양수여야 합니다.")
        chatRoomId: Long,
        @RequestPart
        image: MultipartFile,
    ): ResponseEntity<String> = ResponseEntity.ok(messageCommandService.uploadImage(memberId, chatRoomId, image))
}
