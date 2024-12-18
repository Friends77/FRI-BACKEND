package com.friends.board.repository

import com.friends.board.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByBoardIdAndId(
        boardId: Long,
        id: Long,
    ): Comment?

    fun findTop20ByBoardIdOrderByCreatedAtAsc(boardId: Long): List<Comment>

    fun findTop20ByBoardIdAndCreatedAtAfterOrderByIdAsc(
        boardId: Long,
        createdAt: LocalDateTime,
    ): List<Comment>
}
