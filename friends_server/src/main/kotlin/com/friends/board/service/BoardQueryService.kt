package com.friends.board.service

import com.friends.board.entity.Board
import com.friends.board.repository.BoardCategoryRepository
import com.friends.board.repository.BoardRepository
import com.friends.common.entity.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardQueryService(
    private val boardRepository: BoardRepository,
    private val boardCategoryRepository: BoardCategoryRepository,
) {
    //상세조회
    @Transactional(readOnly = true)
    fun getBoard(id: Long): Pair<Board, List<Category>>? {
        val board = boardRepository.findByIdOrNull(id) ?: return null
        val categories = boardCategoryRepository.findByBoardId(id).map { it.category }

        return Pair(board, categories)
    }

    //전체조회
    fun getBoardList(pageable: Pageable): Page<Board> = boardRepository.findAll(pageable)
}
