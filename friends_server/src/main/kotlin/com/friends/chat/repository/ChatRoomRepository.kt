package com.friends.chat.repository

import com.friends.category.entity.Category
import com.friends.chat.entity.ChatRoom
import com.friends.chat.entity.ChatRoomCategory
import com.friends.common.util.getSlice
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ChatRoomRepository :
    JpaRepository<ChatRoom, Long>,
    ChatRoomCustomRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ChatRoom c WHERE c.id = :id")
    fun findByIdWithLock(id: Long): ChatRoom?
}

interface ChatRoomCustomRepository {
    fun findChatRoomWithCategoryIds(
        categoryIds: List<Long>,
        pageable: Pageable,
    ): Slice<ChatRoom>
}

class ChatRoomCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : ChatRoomCustomRepository {
    override fun findChatRoomWithCategoryIds(
        categoryIds: List<Long>,
        pageable: Pageable,
    ): Slice<ChatRoom> =
        kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(ChatRoom::class))
                .from(entity(ChatRoom::class), innerJoin(ChatRoom::categories))
                .where(path(ChatRoomCategory::category).path(Category::id).`in`(categoryIds))
                .groupBy(path(ChatRoom::id))
                .having(count(path(ChatRoomCategory::id)).eq(categoryIds.size.toLong()))
        }
}
