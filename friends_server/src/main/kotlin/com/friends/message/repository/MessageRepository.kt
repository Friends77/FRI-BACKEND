package com.friends.message.repository

import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.util.find
import com.friends.common.util.getList
import com.friends.common.util.getSingle
import com.friends.common.util.getSlice
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository :
    JpaRepository<Message, Long>,
    MessageCustomRepository {
    fun findFirstByChatRoomOrderByIdDesc(chatRoom: ChatRoom): Message?

    fun deleteByChatRoom(chatRoom: ChatRoom)
}

interface MessageCustomRepository {
    fun countUnreadMessages(
        chatRoomMember: ChatRoomMember,
    ): Int

    fun findUnreadMessagesForMember(
        chatRoomMember: ChatRoomMember,
    ): List<Message>

    fun findMessagesBeforeIdInChatRoom(
        chatRoom: ChatRoom,
        messageId: Long? = null,
        size: Int,
    ): Slice<Message>

    fun findRecentMessageInChatRoom(chatRoom: ChatRoom): Message?
}

class MessageCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : MessageCustomRepository {
    override fun countUnreadMessages(
        chatRoomMember: ChatRoomMember,
    ): Int =
        kotlinJdslJpqlExecutor
            .getSingle {
                select(count(entity(Message::class)))
                    .from(entity(Message::class))
                    .where(
                        and(
                            path(Message::chatRoom).equal(chatRoomMember.chatRoom),
                            path(Message::id).greaterThan(chatRoomMember.lastReadMessage.id),
                            path(Message::type).notEqual(MessageType.SYSTEM), // 시스템 메세지(입장 메세지 등)는 읽지 않은 메세지로 카운트하지 않음
                        ),
                    )
            }.toInt()

    /**
     * 채팅방의 읽지 않은 메세지를 가져옵니다.
     * id 오름차순으로 정렬합니다.
     */
    override fun findUnreadMessagesForMember(chatRoomMember: ChatRoomMember): List<Message> =
        kotlinJdslJpqlExecutor.getList {
            select(entity(Message::class))
                .from(entity(Message::class))
                .where(
                    and(
                        path(Message::chatRoom).equal(chatRoomMember.chatRoom),
                        path(Message::id).greaterThan(chatRoomMember.lastReadMessage.id),
                    ),
                ).orderBy(path(Message::id).asc())
        }

    /**
     * 채팅방의 특정 메세지 이전의 메세지를 가져옵니다.
     * id 오름차순으로 정렬합니다.
     */
    override fun findMessagesBeforeIdInChatRoom(
        chatRoom: ChatRoom,
        messageId: Long?,
        size: Int,
    ): Slice<Message> {
        val pageable = Pageable.ofSize(size)
        val slice =
            kotlinJdslJpqlExecutor.getSlice(pageable) {
                select(entity(Message::class))
                    .from(entity(Message::class))
                    .where(
                        and(
                            path(Message::chatRoom).equal(chatRoom),
                            messageId?.let { path(Message::id).lessThan(it) },
                        ),
                    ).orderBy(path(Message::id).desc()) // 여전히 내림차순으로 정렬
            }

        // 결과를 오름차순으로 변환
        val sortedContent = slice.content.reversed()
        return SliceImpl(sortedContent, pageable, slice.hasNext())
    }

    override fun findRecentMessageInChatRoom(chatRoom: ChatRoom): Message? =
        kotlinJdslJpqlExecutor.find {
            select(entity(Message::class))
                .from(entity(Message::class))
                .where(
                    and(
                        path(Message::chatRoom).equal(chatRoom),
                        path(Message::type).notEqual(MessageType.SYSTEM),
                    ),
                ).orderBy(path(Message::id).desc())
        }
}
