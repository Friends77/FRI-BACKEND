package com.friends.board.service

import com.friends.board.entity.Board
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardQueryService  (
    private val boardRepository: BoardRepository,
    private val boardHashtagRepository: BoardHashtagRepository
){
    //상세조회
    fun getBoard(id: Long): Pair<Board, List<String>>? {

        val board = boardRepository.findByIdOrNull(id) ?: return null
        val hashtags = boardHashtagRepository.findByBoard(board)
            .map { it }

        return Pair(board, hashtags)
    }

    //전체조회
    @Transactional(readOnly = true)
    fun getBoardList(pageable: Pageable): Page<Board> {
        return boardRepository.findAll(pageable)
    }
}