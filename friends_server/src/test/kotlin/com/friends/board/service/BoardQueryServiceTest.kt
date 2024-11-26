package com.friends.board.service

import com.friends.board.BOARD_ID
import com.friends.board.INVALID_BOARD_ID
import com.friends.board.createTestBoard
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import com.friends.board.testHashtags
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.repository.findByIdOrNull

class BoardQueryServiceTest :
        BehaviorSpec({
            val boardQueryService = mockk<BoardQueryService>()
            val boardRepository = mockk<BoardRepository>()
            val boardHashtagRepository = mockk<BoardHashtagRepository>()

            isolationMode = IsolationMode.InstancePerLeaf

            given("getBoard 메서드를 호출할 때"){
                every { boardRepository.findByIdOrNull(BOARD_ID) } returns createTestBoard()
                every { boardHashtagRepository.findByBoard(createTestBoard()) } returns testHashtags
                every { boardQueryService.getBoard(BOARD_ID) } returns Pair(createTestBoard(), testHashtags)

                `when`("존재하는 boardId가 주어졌다면"){
                    val result = boardQueryService.getBoard(BOARD_ID)

                    then("Board와 Board 관련 Hashtag 리스트를 반환해야 한다.") {
                        result?.first shouldBe createTestBoard()
                        result?.second shouldContainExactly testHashtags
                    }
                }

                `when`("존재하지 않는 boardId가 주어졌다면"){
                    val result = boardQueryService.getBoard(INVALID_BOARD_ID)

                    then("null을 반환해야 한다.") {
                        result?.first shouldBe null
                    }
                }
            }
        })