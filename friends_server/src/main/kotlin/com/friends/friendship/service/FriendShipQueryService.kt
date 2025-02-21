package com.friends.friendship.service

import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.common.exception.ErrorCode
import com.friends.common.exception.ParameterValidationException
import com.friends.friendship.repository.FriendShipRepository
import com.friends.profile.dto.ProfileSimpleResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendShipQueryService(
    private val friendShipRepository: FriendShipRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
) {
    @Value("\${image.profile-base-url}")
    lateinit var profileBaseImageUrl: String

    fun getFriendshipListNotInChatRoomByName(
        memberId: Long,
        chatRoomId: Long,
        nickname: String?,
    ): List<ProfileSimpleResponseDto> {
        // 친구 관계가 맺어진 사람들 중 닉네임으로 검색 (닉네임이 null 이라면 전체 검색)
        val friends = friendShipRepository.findFriendshipByMemberIdAndNickname(memberId, nickname)

        // 채팅방에 속한 사람 조회
        val usersInChatRoom = chatRoomMemberRepository.findAllByChatRoomId(chatRoomId)
        // 채팅방에 속해있는 유저 집합
        val usersInChatRoomSet = usersInChatRoom.map { it.member.id }.toSet()

        // 친구들 중 채팅방에 속하지 않은 사람만 추출
        val notInChatRoomFriends =
            friends.filter { friend ->
                friend.id !in usersInChatRoomSet
            }

        // DTO 변환 및 정렬
        return notInChatRoomFriends
            .map { friend ->
                ProfileSimpleResponseDto(
                    memberId = friend.id,
                    nickname = friend.nickname,
                    imageUrl = friend.profile?.imageUrl ?: profileBaseImageUrl,
                    selfDescription = friend.profile?.selfDescription,
                )
            }.sortedBy { it.nickname }
    }

    fun getFriendshipList(
        memberId: Long,
        nickName: String?,
    ): List<ProfileSimpleResponseDto> {
        if (nickName != null && nickName.isBlank()) {
            throw ParameterValidationException(ErrorCode.INVALID_SEARCH_NICKNAME)
        }
        // Friendship 조회
        val friends = friendShipRepository.findFriendshipByMemberIdAndNickname(memberId, nickName)

        // DTO 변환 및 정렬
        return friends
            .map { friend ->
                ProfileSimpleResponseDto(
                    memberId = friend.id,
                    nickname = friend.nickname,
                    imageUrl = friend.profile?.imageUrl ?: profileBaseImageUrl,
                    selfDescription = friend.profile?.selfDescription,
                )
            }.sortedBy { it.nickname }
    }
}
