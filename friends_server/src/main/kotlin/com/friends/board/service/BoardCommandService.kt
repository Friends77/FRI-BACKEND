package com.friends.board.service

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.board.repository.BoardRepository
import com.friends.member.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class BoardCommandService  (
    val boardRepository: BoardRepository,
    val memberRepository: MemberRepository,
){

    @Transactional
    fun save(boardFormDto: BoardFormDto): Board {
        val member =
            memberRepository.findById(boardFormDto.memberId)
                .orElseThrow {
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: ${boardFormDto.memberId}")
                }

        val board =
            Board(
                member = member,
                content = boardFormDto.content,
            )

        return boardRepository.save(board)
    }



    @Transactional
    fun deleteBoard(id: Long)  {
        boardRepository.deleteById(id)
    }

    fun updateBoard(
        id: Long,
        boardFormDto: BoardFormDto,
    ): Board {
        val board = boardRepository.findById(id).get()
        board.updateBoard(boardFormDto)
        return board
    }
}