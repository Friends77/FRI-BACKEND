package com.friends.chat.controller

import com.friends.chat.dto.ChatRoomInvitationHandlerDto
import com.friends.chat.dto.ChatRoomInvitationRequestDto
import com.friends.chat.service.ChatRoomInvitationService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user/chat/invitation")
class ChatRoomInvitationController(
    private val chatRoomInvitationService: ChatRoomInvitationService,
) {
    @PostMapping("/request")
    fun requestInvitation(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody chatRoomInvitationRequestDto: ChatRoomInvitationRequestDto,
    ) {
        chatRoomInvitationService.requestInvitation(memberId, chatRoomInvitationRequestDto)
    }

    @PostMapping("/accept")
    fun acceptInvitation(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto,
    ) {
        chatRoomInvitationService.acceptInvitation(memberId, chatRoomInvitationHandlerDto)
    }

    @PostMapping("/reject")
    fun rejectInvitation(
        @RequestBody chatRoomInvitationHandlerDto: ChatRoomInvitationHandlerDto,
    ) {
        chatRoomInvitationService.rejectInvitation(chatRoomInvitationHandlerDto)
    }
}
