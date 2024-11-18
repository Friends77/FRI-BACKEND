package com.friends.board.service

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.board.repository.BoardRepository
import com.friends.member.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class BoardService
    (
        val boardRepository: BoardRepository,
        val memberRepository: MemberRepository,
    ) {
        @Transactional
        fun save(boardFormDto: BoardFormDto): Board  {
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

        fun getBoard(id: Long): Board {

            val board = boardRepository.findById(id)
                .orElseThrow{
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found with id: $id")
                }

            return boardRepository.findById(id).get()
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

        fun getBoardList(pageable: Pageable): Page<Board>  {
            return boardRepository.findAll(pageable)
        }
    }
