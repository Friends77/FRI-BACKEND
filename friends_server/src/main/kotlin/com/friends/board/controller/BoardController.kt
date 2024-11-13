package com.friends.board.controller

import com.friends.board.dto.BoardFormDto
import com.friends.board.service.BoardService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/board")
class BoardController @Autowired constructor(val boardService: BoardService) {

    //게시글 등록
    @PostMapping
    fun addBoard(boardFormDto: BoardFormDto): ResponseEntity<Any> {
        val save = boardService.save(boardFormDto)
        return ResponseEntity.ok().body(save)
    }

    //게시글 읽기
    @GetMapping("/{id}")
    fun getBoard(@PathVariable id: Long): ResponseEntity<Any> {
        val board = boardService.getBoard(id)
        return ResponseEntity.ok().body(board)
    }

    //게시글 삭제
    @DeleteMapping("/{id}")
    fun deleteBoard(@PathVariable id: Long): ResponseEntity<Any> {
        boardService.deleteBoard(id)
        return ResponseEntity.ok().body(true)
    }

    //게시글 수정
    @PutMapping("/{id}")
    fun updateBoard(@PathVariable id: Long, boardFormDto: BoardFormDto): ResponseEntity<Any> {
        val board = boardService.updateBoard(id, boardFormDto)
        return ResponseEntity.ok().body(board)
    }

    //게시글 목록
    @GetMapping("/list")
    fun listBoards(): ResponseEntity<Any> {
        return ResponseEntity.ok().body(boardService.getBoardList())
    }

}