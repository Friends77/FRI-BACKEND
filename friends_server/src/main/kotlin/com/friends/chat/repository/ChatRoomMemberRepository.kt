package com.friends.chat.repository

import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.util.getList
import com.friends.common.util.getSlice
import com.friends.member.entity.Member
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomMemberRepository :
    JpaRepository<ChatRoomMember, Long>,
    ChatRoomMemberCustomRepository {
    fun countByChatRoom(chatRoom: ChatRoom): Int

    fun findAllByMemberId(memberId: Long): List<ChatRoomMember>

    fun findByChatRoomAndMember(
        chatRoom: ChatRoom,
        member: Member,
    ): ChatRoomMember?

    fun existsChatRoomMemberByChatRoomAndMember(
        chatRoom: ChatRoom,
        member: Member,
    ): Boolean

    fun findAllByMemberId(memberId: Long): List<ChatRoomMember>
}

interface ChatRoomMemberCustomRepository {
    fun sliceChatRoomIdByMember(
        memberId: Long,
        memberList: List<Member>,
        size: Int,
        lastChatRoomMemberId: Long?,
    ): Slice<ChatRoomMember>
}

class ChatRoomMemberCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : ChatRoomMemberCustomRepository {
    override fun sliceChatRoomIdByMember(
        memberId: Long,
        memberList: List<Member>,
        size: Int,
        lastChatRoomMemberId: Long?,
    ): Slice<ChatRoomMember> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(ChatRoomMember::class)) // 중복 제거
                .from(entity(ChatRoomMember::class), join(ChatRoomMember::chatRoom))
                .where(
                    and(
                        path(ChatRoomMember::member).path(Member::id).eq(memberId), // 내가 속한 채팅방
                        dynamicChatRoomList(memberList), // 친구들이 속한 채팅방이어야한다는 조건
                        dynamicLastChatRoomId(lastChatRoomMemberId),
                    ),
                ).orderBy(path(ChatRoomMember::id).desc())
        }
    }

    private fun Jpql.dynamicLastChatRoomId(
        lastChatRoomMemberId: Long?,
    ): Predicate? = if (lastChatRoomMemberId == null) null else path(ChatRoomMember::id).lessThan(lastChatRoomMemberId)

    private fun Jpql.dynamicChatRoomList(
        memberList: List<Member>,
    ): Predicate? =
        if (memberList.isEmpty()) {
            null
        } else {
            path(ChatRoomMember::chatRoom).`in`( // 해당 채팅방이 친구가 속한 채팅방인지 확인
                kotlinJdslJpqlExecutor.getList {
                    // 출력 : 친구들이 속한 채팅방 리스트
                    selectDistinct(path(ChatRoomMember::chatRoom)) // 중복 채팅방 제거
                        .from(entity(ChatRoomMember::class), join(ChatRoomMember::chatRoom))
                        .where(path(ChatRoomMember::member).`in`(memberList))
                },
            )
        }
}
