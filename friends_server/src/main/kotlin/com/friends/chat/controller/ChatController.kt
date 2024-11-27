package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomRequest
import com.friends.chat.service.ChatRoomCommandService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/user/chat")
class ChatController(
    private val chatRoomCommandService: ChatRoomCommandService,
) : ChatControllerSpec {
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun createChatRoom(
        chatRoomRequest: ChatRoomRequest,
        backgroundImage: MultipartFile?,
        memberId: Long,
    ): ResponseEntity<Void> {
        chatRoomCommandService.createChatRoom(chatRoomRequest, memberId, backgroundImage)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
