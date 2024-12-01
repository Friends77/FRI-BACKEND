package com.friends.board.controller

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.board.service.BoardCommandService
import com.friends.board.service.BoardQueryService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BoardController(
    val boardCommandService: BoardCommandService,
    val boardQueryService: BoardQueryService,
) {
    // 게시글 등록
    @PostMapping("api/user/board")
    fun createBoard(
        @RequestBody @Valid boardFormDto: BoardFormDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void> {
        boardCommandService.createBoard(boardFormDto, memberId)
        return ResponseEntity.noContent().build()
    }

    // 게시글 상세조회
    @GetMapping("api/board/{id}")
    fun getBoard(
        @PathVariable id: Long,
    ): ResponseEntity<BoardFormDto> {
        val (board, hashtags) =
            boardQueryService.getBoard(id)
                ?: return ResponseEntity.notFound().build()

        val boardDto =
            BoardFormDto(
                content = board.content,
                hashtags = hashtags,
            )
        return ResponseEntity.ok(boardDto)
    }

    // 게시글 삭제
    @DeleteMapping("api/user/board/{id}")
    fun deleteBoard(
        @PathVariable id: Long,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void> {
        boardCommandService.deleteBoard(id, memberId)
        return ResponseEntity.noContent().build()
    }

    // 게시글 수정
    @PutMapping("api/user/board/{id}")
    fun updateBoard(
        @PathVariable id: Long,
        @RequestBody boardFormDto: BoardFormDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Board> {
        val board = boardCommandService.updateBoard(id, boardFormDto, memberId)
        return ResponseEntity.ok(board)
    }

    // 게시글 전체조회
    @GetMapping("api/board/list")
    fun getBoards(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
    ): Page<Board> {
        val pageable = PageRequest.of(page, pageSize)
        return boardQueryService.getBoardList(pageable)
    }
}
