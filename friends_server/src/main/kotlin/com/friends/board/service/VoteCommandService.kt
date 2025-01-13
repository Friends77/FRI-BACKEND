package com.friends.board.service

import com.friends.board.entity.Vote
import com.friends.board.entity.VoteOption
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.exception.VoteNotFoundException
import com.friends.board.exception.VoteOptionNotFoundException
import com.friends.board.exception.VoteOptionPositiveCountException
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.VoteOptionRepository
import com.friends.board.repository.VoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VoteCommandService(
    private val voteRepository: VoteRepository,
    private val boardRepository: BoardRepository,
    private val voteOptionRepository: VoteOptionRepository,
) {
    /**
     * 게시글에 투표 추가하기
     */
    fun addVoteToBoard(
        boardId: Long,
        voteOptionContent: List<String>,
    ): Vote {
        val board =
            boardRepository.findById(boardId)
                .orElseThrow { BoardNotFoundException() }
        val vote = Vote(board = board)
        vote.options.addAll(
            voteOptionContent.map { content ->
                VoteOption(vote = vote, content = content)
            },
        )
        return voteRepository.save(vote)
    }

    /**
     * 투표 선택지 추가하기
     */
    fun addVoteOption(
        voteId: Long,
        optionContent: String,
    ): VoteOption {
        val vote =
            voteRepository.findById(voteId)
                .orElseThrow { VoteNotFoundException() }
        val newOption = VoteOption(vote = vote, content = optionContent)
        vote.options.add(newOption)
        voteRepository.save(vote)
        return newOption
    }

    /**
     * 투표 선택지 삭제하기
     */
    fun deleteVoteOption(
        voteId: Long,
        optionId: Long,
    ) {
        val vote =
            voteRepository.findById(voteId)
                .orElseThrow { VoteNotFoundException() }
        val optionToDelete =
            vote.options.find { it.id == optionId }
                ?: throw VoteOptionNotFoundException()
        vote.options.remove(optionToDelete)
        voteRepository.save(vote)
    }

    /**
     * 투표 삭제하기
     */
    fun deleteVote(voteId: Long) {
        if (!voteRepository.existsById(voteId)) {
            throw VoteNotFoundException()
        }
        voteRepository.deleteById(voteId)
    }

    /**
     * 투표 선택지의 투표수 증가시키기
     */
    fun incrementVoteCount(optionId: Long) {
        val option =
            voteOptionRepository.findOptionById(optionId)
                ?: throw VoteOptionNotFoundException()
        option.voteCount += 1
        voteOptionRepository.saveOption(option)
    }

    /**
     * 투표 선택지의 투표수 감소시키기
     */
    fun decrementVoteCount(optionId: Long) {
        val option =
            voteOptionRepository.findOptionById(optionId)
                ?: throw VoteOptionNotFoundException()
        if (option.voteCount > 0) {
            option.voteCount -= 1
            voteOptionRepository.saveOption(option)
        } else {
            throw VoteOptionPositiveCountException()
        }
    }
}
