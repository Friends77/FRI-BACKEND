package com.friends.board.service

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.board.entity.BoardHashtag
import com.friends.board.entity.Hashtag
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.HashtagRepository
import com.friends.member.repository.MemberRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class BoardCommandService(
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val hashtagRepository: HashtagRepository,
    private val boardHashtagRepository: BoardHashtagRepository
){

    fun createBoard(boardFormDto: BoardFormDto, requestMemberId: Long): Board {
        val member =
            memberRepository.findById(requestMemberId)
                .orElseThrow {
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found with id: ${requestMemberId}")
                }

        val board = Board(
                member = member,
                content = boardFormDto.content,
            )

        boardRepository.save(board)

        val hashtags = boardFormDto.hashtags.map { tag ->
            hashtagRepository.findByTag(tag) ?: hashtagRepository.save(Hashtag(tag = tag))
        }

        // 게시글-해시태그 관계 설정
        val boardHashtags = hashtags.map { hashtag ->
            BoardHashtag(
                board = board,
                hashtag = hashtag,
            )
        }

        boardHashtagRepository.saveAll(boardHashtags)

        return board
    }

    fun deleteBoard(id: Long, requestMemberId: Long)  {
        val board = boardRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found with id: $id")
        }

        //요청자와 작성자 비교
        if(board.member.id != requestMemberId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Must be able to delete member.")
        }

        boardRepository.deleteById(id)
    }

    fun updateBoard(
        id: Long,
        boardFormDto: BoardFormDto,
        requestMemberId: Long
    ): Board {
        val board = boardRepository.findById(id).orElseThrow{
            ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found with id: $id")
        }

        //요청자와 작성자 비교
        if(board.member.id != requestMemberId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Member with id: $requestMemberId not found")
        }

        board.updateBoard(boardFormDto)
        return board
    }
}