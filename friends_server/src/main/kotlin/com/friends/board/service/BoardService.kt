package com.friends.board.service

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.board.repository.BoardRepository
import com.friends.member.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoardService
    (
        val boardRepository: BoardRepository,
        val memberRepository: MemberRepository,
    ) {
        fun save(boardFormDto: BoardFormDto): Board  {
            val member =
                memberRepository.findById(boardFormDto.memberId)
                    .orElseThrow { IllegalArgumentException("Member not found with id: ${boardFormDto.memberId}") }

            val board =
                Board(
                    member = member,
                    content = boardFormDto.content,
                )

            return boardRepository.save(board)
        }

        fun getBoard(id: Long): Board {
            return boardRepository.findById(id).get()
        }

        fun deleteBoard(id: Long)  {
            return boardRepository.deleteById(id)
        }

        fun updateBoard(
            id: Long,
            boardFormDto: BoardFormDto,
        ): Board {
            val board = boardRepository.findById(id).get()
            board.updateBoard(boardFormDto)
            return board
        }

        fun getBoardList(): List<Board>  {
            return boardRepository.findAll()
        }
    }
