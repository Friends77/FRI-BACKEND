package com.friends.message.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.common.dto.SliceBaseResponse
import com.friends.message.dto.MessageResponseDto
import com.friends.message.service.MessageQueryService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/message")
class MessageController(
    private val messageQueryService: MessageQueryService,
) {
    @GetMapping("/unread/{chatRoomId}")
    fun getUnreadMessageCount(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable chatRoomId: Long,
    ): ResponseEntity<ListBaseResponse<MessageResponseDto>> {
        val result = messageQueryService.getUnreadMessage(memberId, chatRoomId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/previous/{chatRoomId}")
    fun getPreviousMessage(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable chatRoomId: Long,
        @RequestParam("size", defaultValue = "20") size: Int,
        @RequestParam("lastMessageId", required = false) lastMessageId: Long?,
    ): ResponseEntity<SliceBaseResponse<MessageResponseDto>> {
        val result = messageQueryService.getPreviousMessages(chatRoomId, memberId, lastMessageId, size)
        return ResponseEntity.ok(result)
    }
}
