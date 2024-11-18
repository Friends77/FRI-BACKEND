package com.friends.board.controller

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.board.service.BoardService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/board")
class BoardController(
    val boardService: BoardService
) {
        // 게시글 등록
        @PostMapping
        fun createBoard(@RequestBody @Valid boardFormDto: BoardFormDto): ResponseEntity<Void> {
//            val savedBoard = boardService.save(boardFormDto)
//            return ResponseEntity.ok().body(savedBoard)
            return ResponseEntity.noContent().build()
        }

        // 게시글 상세조회
        @GetMapping("/{id}")
        fun getBoard(
            @PathVariable id: Long,
        ): ResponseEntity<Board> {
            val board = boardService.getBoard(id)
            return ResponseEntity.ok().body(board)
        }

        // 게시글 삭제
        @DeleteMapping("/{id}")
        fun deleteBoard(
            @PathVariable id: Long,
        ): ResponseEntity<Void> {
            boardService.deleteBoard(id)
            return ResponseEntity.noContent().build()
        }

        // 게시글 수정
        @PutMapping("/{id}")
        fun updateBoard(
            @PathVariable id: Long,
            boardFormDto: BoardFormDto,
        ): ResponseEntity<Board> {
            val board = boardService.updateBoard(id, boardFormDto)
            return ResponseEntity.ok().body(board)
        }

        // 게시글 전체조회
        @GetMapping("/list")
        fun getBoards(
            @RequestParam(defaultValue = "0") page: Int,
            @RequestParam(defaultValue = "10") pageSize: Int
        ): Page<Board> {
            val pageable = PageRequest.of(page, pageSize)
            return boardService.getBoardList(pageable)
        }
    }
