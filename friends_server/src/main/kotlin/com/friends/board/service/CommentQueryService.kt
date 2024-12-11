package com.friends.board.service

import com.friends.board.entity.Comment
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.repository.BoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentQueryService (
    private val boardRepository: BoardRepository,
){
    //댓글조회
    @Transactional(readOnly = true)
    fun getCommentList(
        boardId: Long,
    ): List<Comment> {
        val board = boardRepository.findById(boardId).orElseThrow{
            throw BoardNotFoundException()
        }
        return board.comments.sortedBy { it.createdAt }
    }
}