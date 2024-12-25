package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomCreateRequestDto
import com.friends.chat.dto.ChatRoomInfoResponseDto
import com.friends.chat.service.ChatRoomCommandService
import com.friends.chat.service.ChatRoomQueryService
import com.friends.common.dto.SliceBaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
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
        chatRoomCreateRequestDto: ChatRoomCreateRequestDto,
        backgroundImage: MultipartFile?,
        memberId: Long,
    ): ResponseEntity<Void> {
        chatRoomCommandService.createChatRoom(chatRoomCreateRequestDto, memberId, backgroundImage)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    override fun getChatRooms(
        memberId: Long,
        size: Int,
        lastChatRoomMemberId: Long?,
        nickname: String?,
    ): ResponseEntity<SliceBaseResponse<ChatRoomInfoResponseDto>> = ResponseEntity.ok(chatRoomQueryService.getChatRooms(memberId, size, lastChatRoomMemberId, nickname))
}
