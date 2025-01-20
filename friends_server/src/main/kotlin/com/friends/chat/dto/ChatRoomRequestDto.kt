package com.friends.chat.dto

import com.friends.common.annotation.NullOrNotBlank
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

data class ChatRoomCreateRequestDto(
    @Schema(description = "채팅방 제목")
    @field:NotBlank(message = "채팅방 제목은 공백일 수 없습니다.")
    @field:Length(min = 1, max = 20, message = "채팅방 제목은 1자 이상 20자 이하로 입력해주세요.") //TODO: 제목 최대 길이 제한
    val title: String,
    @field:Size(min = 1, message = "채팅방 카테고리는 최소 1개 이상 선택해주세요.") //TODO: 카테고리 최대 개수 제한
    @Schema(description = "채팅방 카테고리 ID 리스트")
    val categoryIdList: Set<Long>,
)

data class ChatRoomUpdateRequestDto(
    @Schema(description = "안 바뀌면 null로 보내주세요.")
    @field:NullOrNotBlank(message = "채팅방 제목은 공백일 수 없습니다.") // null 허용, 공백은 안됨
    @field:Length(min = 1, max = 20, message = "채팅방 제목은 1자 이상 20자 이하로 입력해주세요.")
    val title: String?,
    @Schema(description = "안 바뀌면 null로 보내주세요. 바뀌면 카테고리 ID 리스트 전체를 보내주세요.")
    val categoryIdList: Set<Long>?,
    @Schema(description = "기존 배경 이미지 삭제시 true, 변경 안 할 시 false)")
    val backgroundImageDelete: Boolean,
)

data class ChatRoomInvitationRequestDto(
    @Schema(description = "초대할 채팅방의 id")
    val chatRoomId: Long,
    @Schema(description = "초대할 사용자 ID 리스트")
    val receiverIdList: Set<Long>,
)

data class ChatRoomInvitationHandlerDto(
    @Schema(defaultValue = "초대받은 알람의 id")
    val alarmId: Long,
)
