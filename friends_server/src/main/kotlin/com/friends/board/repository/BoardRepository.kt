package com.friends.board.repository

import com.friends.board.entity.BoardEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardRepository: JpaRepository<BoardEntity, Long> {
}