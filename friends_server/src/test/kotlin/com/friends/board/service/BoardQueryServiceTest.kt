package com.friends.board.service

import com.friends.board.entity.Board
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
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

            given("유효한 boardId가 주어졌을 때"){
                val boardId = 1L
                val member = Member(id = 1L, name = "Test Member", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE, imageUrl = "test imageurl")
                val mockBoard = Board(id = boardId, member = member, content = "Test content")
                val mockHashtags = listOf("friends", "kotlin")

                every { boardRepository.findByIdOrNull(boardId) } returns mockBoard
                every { boardHashtagRepository.findByBoard(mockBoard) } returns mockHashtags
                every { boardQueryService.getBoard(boardId) } returns Pair(mockBoard, mockHashtags)

                `when`("getBoard 메서드를 호출하면"){
                    val result = boardQueryService.getBoard(boardId)

                    then("Board와 Board 관련 Hashtag 리스트를 반환해야 한다.") {
                        result?.first shouldBe mockBoard
                        result?.second shouldContainExactly mockHashtags
                    }
                }
            }

            given("존재하지 않는 boardId가 주어졌을 때") {
                val invalidBoardId = 99L

                every { boardRepository.findByIdOrNull(invalidBoardId) } returns null
                every { boardQueryService.getBoard(invalidBoardId) } returns null

                `when`("getBoard 메서드를 호출하면") {
                    val result = boardQueryService.getBoard(invalidBoardId)

                    then("null을 반환해야한다.") {
                        result shouldBe null
                    }
                }
            }

        })