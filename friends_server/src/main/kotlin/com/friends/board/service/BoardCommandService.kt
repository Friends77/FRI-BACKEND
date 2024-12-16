package com.friends.board.service

import com.friends.board.dto.BoardRequestFormDto
import com.friends.board.entity.Board
import com.friends.board.entity.BoardCategory
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.exception.InvalidBoardAccessException
import com.friends.board.exception.NotFoundBoardCategoryException
import com.friends.board.repository.BoardCategoryRepository
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.CategoryRepository
import com.friends.category.entity.Category
import com.friends.member.repository.MemberRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class BoardCommandService(
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val boardCategoryRepository: BoardCategoryRepository,
) {
    fun createBoard(
        boardFormDto: BoardRequestFormDto,
        requestMemberId: Long,
    ): Board {
        val member =
            memberRepository
                .findById(requestMemberId)
                //머지 후 membernotfoundexception으로 대체
                .orElseThrow {
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: $requestMemberId")
                }

        val board =
            Board(
                member = member,
                content = boardFormDto.content,
            )

        boardRepository.save(board)

        val categories =
            categoryRepository.findByIdIn(boardFormDto.categoryIds).also {
                if (it.isEmpty()) {
                    throw NotFoundBoardCategoryException()
                }
            }
        // 게시글-해시태그 관계 설정
        val boardCategories =
            categories.map { category ->
                BoardCategory(
                    board = board,
                    category = Category(category.id, category.name, category.type),
                )
            }
        boardCategoryRepository.saveAll(boardCategories)
        return board
    }

    fun deleteBoard(
        id: Long,
        requestMemberId: Long,
    ) {
        val board =
            boardRepository.findById(id).orElseThrow {
                BoardNotFoundException()
            }

        //요청자와 작성자 비교
        if (board.member.id != requestMemberId) {
            throw InvalidBoardAccessException()
        }

        boardRepository.deleteById(id)
    }

    fun updateBoard(
        id: Long,
        boardFormDto: BoardRequestFormDto,
        requestMemberId: Long,
    ): Board {
        val board =
            boardRepository.findById(id).orElseThrow {
                BoardNotFoundException()
            }

        //요청자와 작성자 비교
        if (board.member.id != requestMemberId) {
            throw InvalidBoardAccessException()
        }
        boardCategoryRepository.deleteByBoardId(id)
        boardCategoryRepository.saveAll(
            categoryRepository.findByIdIn(boardFormDto.categoryIds).map {
                BoardCategory(
                    board = board,
                    category = Category(it.id, it.name, it.type),
                )
            },
        )
        board.updateBoard(boardFormDto)

        return board
    }
}
