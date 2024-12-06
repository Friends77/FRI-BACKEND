package com.friends.board.controller

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Board", description = "게시판 API")
interface BoardControllerSpec {
    @Operation(
        description = "게시판 등록",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "게시판 등록 성공",
            ),
        ],
    )
    fun createBoard(
        @RequestBody @Valid boardFormDto: BoardFormDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void>

    //TODO: 여기서부턴 작성해야함
    fun getBoard(
        @PathVariable id: Long,
    ): ResponseEntity<BoardFormDto>

    fun deleteBoard(
        @PathVariable id: Long,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Void>

    fun updateBoard(
        @PathVariable id: Long,
        @RequestBody boardFormDto: BoardFormDto,
        @AuthenticationPrincipal memberId: Long,
    ): ResponseEntity<Board>

    fun getBoards(
        @RequestParam page: Int,
        @RequestParam size: Int,
    ): Page<Board>
}
