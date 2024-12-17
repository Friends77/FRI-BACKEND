package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomDetailResponseDto
import com.friends.chat.service.ChatRoomQueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/global/chat/room")
class ChatRoomGlobalController(
    private val chatRoomQueryService: ChatRoomQueryService,
) : ChatRoomGlobalControllerSpec {
    @GetMapping("/{id}")
    override fun getChatRoomDetail(
        @PathVariable
        id: Long,
    ): ResponseEntity<ChatRoomDetailResponseDto> = ResponseEntity.ok(chatRoomQueryService.getChatRoom(id))
}
