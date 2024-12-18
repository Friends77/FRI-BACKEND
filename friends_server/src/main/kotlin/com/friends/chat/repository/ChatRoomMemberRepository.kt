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
}

interface ChatRoomMemberCustomRepository {
    fun sliceChatRoomIdByMember(
        memberId: Long,
        chatRoomList: List<ChatRoom>,
        size: Int,
        lastChatRoomId: Long?,
    ): Slice<ChatRoomMember>

    fun findChatRoomByMemberListIn(friendsList: List<Member>): List<ChatRoom>
}

class ChatRoomMemberCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : ChatRoomMemberCustomRepository {
    override fun sliceChatRoomIdByMember(
        memberId: Long,
        chatRoomList: List<ChatRoom>,
        size: Int,
        lastChatRoomId: Long?,
    ): Slice<ChatRoomMember> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(ChatRoomMember::class))
                .from(entity(ChatRoomMember::class), join(ChatRoomMember::chatRoom))
                .where(
                    and(
                        path(ChatRoomMember::member).path(Member::id).eq(memberId),
                        dynamicChatRoomList(chatRoomList),
                        dynamicLastChatRoomId(lastChatRoomId),
                    ),
                ).orderBy(path(ChatRoomMember::id).desc())
        }
    }

    override fun findChatRoomByMemberListIn(friendsList: List<Member>): List<ChatRoom> =
        kotlinJdslJpqlExecutor.getList {
            select(path(ChatRoomMember::chatRoom))
                .from(entity(ChatRoomMember::class), join(ChatRoomMember::chatRoom))
                .where(path(ChatRoomMember::member).`in`(friendsList))
        }

    private fun Jpql.dynamicLastChatRoomId(
        lastId: Long?,
    ): Predicate? = if (lastId == null) null else path(ChatRoomMember::chatRoom)(ChatRoom::id).lessThan(lastId)

    private fun Jpql.dynamicChatRoomList(
        chatRoomList: List<ChatRoom>,
    ): Predicate? = if (chatRoomList.isEmpty()) null else path(ChatRoomMember::chatRoom).`in`(chatRoomList)
}
