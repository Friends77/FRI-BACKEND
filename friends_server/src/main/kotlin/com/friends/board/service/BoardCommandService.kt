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
    fun deleteBoard(id: Long, requestMemberId: Long)  {
        val board = boardRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found with id: $id")
        }

        //요청자와 작성자 비교
        if(board.member.id != requestMemberId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Member with id: $requestMemberId not found")
        }

        boardRepository.deleteById(id)
    }

    fun updateBoard(
        id: Long,
        boardFormDto: BoardFormDto,
        requestMemberId: Long
    ): Board {
        val board = boardRepository.findById(id).orElseThrow{
            ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found with id: $id")
        }

        //요청자와 작성자 비교
        if(board.member.id != requestMemberId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Member with id: $requestMemberId not found")
        }

        board.updateBoard(boardFormDto)
        return board
    }
}