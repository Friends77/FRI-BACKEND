package com.friends.board.service

import com.friends.board.entity.Board
import com.friends.board.repository.BoardRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class BoardQueryService  (
    val boardRepository: BoardRepository,
){
    fun getBoard(id: Long): Board {

        val board = boardRepository.findById(id)
            .orElseThrow{
                ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found with id: $id")
            }

        return boardRepository.findById(id).get()
    }

    fun getBoardList(pageable: Pageable): Page<Board> {
        return boardRepository.findAll(pageable)
    }
}