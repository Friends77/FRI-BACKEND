package com.friends.board.repository

import com.friends.board.entity.Vote
import com.friends.board.entity.VoteOption
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<Vote, Long> {
    fun findOptionById(optionId: Long): VoteOption?
    fun saveOption(option: VoteOption): VoteOption
}