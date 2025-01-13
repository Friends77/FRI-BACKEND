package com.friends.board.repository

import com.friends.board.entity.Vote
import com.friends.board.entity.VoteOption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VoteRepository : JpaRepository<Vote, Long> {
    fun findByBoardId(boardId: Long): Vote?
}
