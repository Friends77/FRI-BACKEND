package com.friends.message.repository

import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomMember
import com.friends.common.util.getSingle
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository :
    JpaRepository<Message, Long>,
    MessageCustomRepository {
    fun findFirstByChatRoomOrderByIdDesc(chatRoom: ChatRoom): Message?
}

interface MessageCustomRepository {
    fun countUnreadMessages(
        chatRoomMember: ChatRoomMember,
    ): Int
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
}
