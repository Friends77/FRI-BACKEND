package com.friends.board.service

import com.friends.board.BoardNotFoundException
import com.friends.board.InvalidBoardAccessException
import com.friends.board.dto.BoardAddDto
import com.friends.board.dto.BoardUpdateDto
import com.friends.board.entity.Board
import com.friends.board.entity.BoardHashtag
import com.friends.board.entity.Hashtag
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.HashtagRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BoardCommandService(
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val hashtagRepository: HashtagRepository,
    private val boardHashtagRepository: BoardHashtagRepository,
) {
    fun createBoard(
        boardAddDto: BoardAddDto,
        requestMemberId: Long,
    ): Board {
        val member =
            memberRepository.findById(requestMemberId)
                .orElseThrow {
                    MemberNotFoundException()
                }
        val board =
            Board(
                member = member,
                content = boardAddDto.content,
            )
        boardRepository.save(board)
        val hashtags =
            boardAddDto.hashtags.map { tag ->
                hashtagRepository.findByTag(tag) ?: hashtagRepository.save(Hashtag(tag = tag))
            }
        // 게시글-해시태그 관계 설정
        val boardHashtags =
            hashtags.map { hashtag ->
                BoardHashtag(
                    board = board,
                    hashtag = hashtag,
                )
            }
        boardHashtagRepository.saveAll(boardHashtags)
        return board
    }

    //게시글 삭제
    fun deleteBoard(
        id: Long,
        requestMemberId: Long,
    ) {
        val board =
            boardRepository.findById(id).orElseThrow {
                BoardNotFoundException()
            }
        if (board.member.id != requestMemberId) {
            throw InvalidBoardAccessException()
        }
        boardRepository.deleteById(id)
    }

    //게시글 수정
    fun updateBoard(
        id: Long,
        boardUpdateDto: BoardUpdateDto,
        requestMemberId: Long,
    ): Board {
        val board =
            boardRepository.findById(id).orElseThrow {
                BoardNotFoundException()
            }
        if (board.member.id != requestMemberId) {
            throw InvalidBoardAccessException()
        }
        board.updateBoard(boardUpdateDto)
        return board
    }
}
