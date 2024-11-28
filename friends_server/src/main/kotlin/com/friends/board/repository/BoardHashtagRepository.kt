package com.friends.board.repository

import com.friends.board.entity.BoardHashtag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BoardHashtagRepository : JpaRepository<BoardHashtag, Long> {
    fun findByBoardId(id : Long): BoardHashtag
}