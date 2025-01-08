package com.friends.board.service

import com.friends.board.BoardNotFoundException
import com.friends.board.entity.Vote
import com.friends.board.entity.VoteOption
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.VoteRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteCommandService (
    private val voteRepository: VoteRepository,
    private val boardRepository: BoardRepository,
){
    /**
      * 게시글에 투표 추가하기
      */
    @Transactional
    fun addVoteToBoard(boardId: Long, voteOptions: List<String>): Vote {
        val board = boardRepository.findById(boardId)
            .orElseThrow{throw BoardNotFoundException()}
        val vote = Vote(board = board)
        vote.options.addAll(
            voteOptions.map { content ->
                VoteOption(vote = vote, content = content)
            }
        )
        return voteRepository.save(vote)
    }

    /**
     * 투표 선택지 추가하기
     */

}