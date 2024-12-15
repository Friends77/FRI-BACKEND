package com.friends.board.service

import com.friends.board.INVALID_BOARD_ID
import com.friends.board.NON_AUTHORIZED_MEMBER_ID
import com.friends.board.REQUEST_MEMBER_ID
import com.friends.board.boardFormDto
import com.friends.board.createTestBoard
import com.friends.board.createTestMember
import com.friends.board.entity.BoardHashtag
import com.friends.board.exception.BoardNotFoundException
import com.friends.board.exception.InvalidBoardAccessException
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.HashtagRepository
import com.friends.member.repository.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class BoardCommandServiceTest :
    BehaviorSpec({
        val boardRepository = mockk<BoardRepository>()
        val memberRepository = mockk<MemberRepository>()
        val hashtagRepository = mockk<HashtagRepository>()
        val boardHashtagRepository = mockk<BoardHashtagRepository>()

        val boardCommandService =
            BoardCommandService(
                boardRepository = boardRepository,
                memberRepository = memberRepository,
                hashtagRepository = hashtagRepository,
                boardHashtagRepository = boardHashtagRepository,
            )

        given("createBoard 메서드를 호출할 때") {
            every { memberRepository.findById(REQUEST_MEMBER_ID) } returns Optional.of(createTestMember())
            every { boardRepository.save(any()) } answers { firstArg() }
            every { hashtagRepository.findByTag(any()) } answers { null } //모든 해시태그는 새로 생성됩니다.
            every { hashtagRepository.save(any()) } answers { firstArg() } //저장된 해시태그 반환
            every { boardHashtagRepository.saveAll(any<List<BoardHashtag>>()) } answers { firstArg() }

            val testMember = createTestMember()

            `when`("유효한 작성자가 글을 작성했으면") {
                val result = boardCommandService.createBoard(boardFormDto, REQUEST_MEMBER_ID)

                then("게시글이 저장되고 반환되어야 한다.") {
                    result.content shouldBe boardFormDto.content
                    result.member === testMember
                }
            }
        }

        given("deleteBoard 메서드를 호출할 때") {
            every { boardRepository.findById(createTestBoard().id) } returns Optional.of(createTestBoard())
            every { boardRepository.deleteById(createTestBoard().id) } returns Unit

            `when`("삭제하려는 회원이 해당 게시글의 작성자라면") {
                boardCommandService.deleteBoard(createTestBoard().id, REQUEST_MEMBER_ID)

                then("게시글이 삭제되어야 한다.") {
                    io.mockk.verify { boardRepository.deleteById(createTestBoard().id) }
                }
            }

            `when`("삭제하려는 회원이 해당 게시글의 작성자가 아니라면") {

                then("권한 없음 예외가 발생해야 한다.") {
                    val exception =
                        assertThrows<InvalidBoardAccessException> {
                            boardCommandService.deleteBoard(createTestBoard().id, NON_AUTHORIZED_MEMBER_ID)
                        }
                    exception.message shouldBe "게시글에 대한 유효하지 않은 접근입니다."
                }
            }

            `when`("boardId가 존재하지 않다면") {
                every { boardRepository.findById(any()) } returns Optional.empty()

                then("예외가 발생해야 한다.") {
                    val exception =
                        assertThrows<BoardNotFoundException> {
                            boardCommandService.deleteBoard(INVALID_BOARD_ID, REQUEST_MEMBER_ID)
                        }
                    exception.message shouldBe "존재하지 않는 게시물입니다."
                }
            }
        }

        given("updateBoard 메서드를 호출할 때") {
            every { boardRepository.findById(createTestBoard().id) } returns Optional.of(createTestBoard())
            every { boardRepository.deleteById(createTestBoard().id) } returns Unit

            `when`("수정하려는 회원이 해당 게시글의 작성자라면") {
                val updatedContent = "Updated content"
                val updatedDto = boardFormDto.copy(content = updatedContent)
                createTestBoard().content = updatedContent

                val result = boardCommandService.updateBoard(createTestBoard().id, updatedDto, REQUEST_MEMBER_ID)

                then("게시글의 내용이 수정되어야 한다.") {
                    result.content shouldBe updatedContent
                }
            }

            `when`("수정하려는 회원이 해당 게시글의 작성자가 아니라면") {

                then("권한 없음 예외가 발생해야 한다.") {
                    val exception =
                        assertThrows<InvalidBoardAccessException> {
                            boardCommandService.updateBoard(createTestBoard().id, boardFormDto, NON_AUTHORIZED_MEMBER_ID)
                        }
                    exception.message shouldBe "게시글에 대한 유효하지 않은 접근입니다."
                }
            }
        }
    })
