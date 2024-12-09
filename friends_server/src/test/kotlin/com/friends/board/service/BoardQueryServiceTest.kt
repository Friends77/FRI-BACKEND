package com.friends.board.service

import com.friends.board.BOARD_ID
import com.friends.board.INVALID_BOARD_ID
import com.friends.board.PAGEABLE
import com.friends.board.createBoardHashtag
import com.friends.board.createTestBoard
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.repository.findByIdOrNull

class BoardQueryServiceTest :
    BehaviorSpec({
        val boardRepository = mockk<BoardRepository>()
        val boardHashtagRepository = mockk<BoardHashtagRepository>()
        val boardQueryService = BoardQueryService(boardRepository, boardHashtagRepository)

        isolationMode = IsolationMode.InstancePerLeaf

        given("getBoard 메서드를 호출할 때") {

            val testBoard = createTestBoard()
            val testBoardHashtags = createBoardHashtag()

            every { boardRepository.findByIdOrNull(BOARD_ID) } returns testBoard
            every { boardHashtagRepository.findByBoardId(BOARD_ID) } returns testBoardHashtags

            `when`("존재하는 boardId가 주어졌다면") {
                val expTags = listOf("testTag")
                val result = boardQueryService.getBoard(BOARD_ID)

                then("Board와 Board 관련 Hashtag 리스트를 반환해야 한다.") {
                    result?.first shouldBe testBoard
                    result?.second shouldContainExactly expTags
                }
            }

            `when`("존재하지 않는 boardId가 주어졌다면") {
                every { boardRepository.findByIdOrNull(INVALID_BOARD_ID) } returns null

                val result = boardQueryService.getBoard(INVALID_BOARD_ID)

                then("null을 반환해야 한다.") {
                    result?.first shouldBe null
                }
            }
        }

        given("getBoardList 메서드를 호출할 때") {
            val testBoard = createTestBoard()

            every { boardRepository.findAll(PAGEABLE) } returns PageImpl(listOf(testBoard))

            `when`("board의 개수가 0이 아니라면") {
                val result = boardQueryService.getBoardList(PAGEABLE)

                then("boardList를 반환해야한다.") {
                    result.content shouldBe listOf(testBoard)
                }
            }
        }
    })
