package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.service.ChatRoomCommandService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/user/chat")
class ChatRoomController(
    private val chatRoomCommandService: ChatRoomCommandService,
) : ChatRoomControllerSpec {
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun createChatRoom(
        chatRoomCreateRequestDto: ChatRoomCreateRequestDto,
        backgroundImage: MultipartFile?,
        memberId: Long,
    ): ResponseEntity<Void> {
        chatRoomCommandService.createChatRoom(chatRoomCreateRequestDto, memberId, backgroundImage)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PostMapping("/{chatRoomId}")
    override fun enterChatRoom(
        @PathVariable
        chatRoomId: Long,
        memberId: Long,
    ): ResponseEntity<Void> {
        chatRoomCommandService.enterChatRoom(chatRoomId, memberId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
