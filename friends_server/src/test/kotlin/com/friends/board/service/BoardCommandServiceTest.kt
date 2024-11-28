package com.friends.board.service

import com.friends.board.REQUEST_MEMBER_ID
import com.friends.board.boardFormDto
import com.friends.board.createTestBoard
import com.friends.board.createTestMember
import com.friends.board.entity.BoardHashtag
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.HashtagRepository
import com.friends.member.repository.MemberRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.Optional

class BoardCommandServiceTest :
        BehaviorSpec ({
            val boardRepository = mockk<BoardRepository>()
            val memberRepository = mockk<MemberRepository>()
            val hashtagRepository = mockk<HashtagRepository>()
            val boardHashtagRepository = mockk<BoardHashtagRepository>()

            val boardCommandService = BoardCommandService(
                boardRepository = boardRepository,
                memberRepository = memberRepository,
                hashtagRepository = hashtagRepository,
                boardHashtagRepository = boardHashtagRepository
            )

            isolationMode = IsolationMode.InstancePerLeaf

            given("createBoard 메서드를 호출할 때") {
                every { memberRepository.findById(REQUEST_MEMBER_ID) } returns Optional.of(createTestMember())
                every { boardRepository.save(any()) } answers { firstArg() }
                every { hashtagRepository.findByTag(any()) } answers { null } //모든 해시태그는 새로 생성됩니다.
                every { hashtagRepository.save(any()) } answers { firstArg() } //저장된 해시태그 반환
                every { boardHashtagRepository.saveAll(any<List<BoardHashtag>>()) } answers { firstArg() }

                `when` ("유효한 작성자가 글을 작성했으면") {
                    val result = boardCommandService.createBoard(boardFormDto, REQUEST_MEMBER_ID)

                    then("게시글이 저장되고 반환되어야 한다.") {
                        result.content shouldBe boardFormDto.content
                        result.member shouldBe createTestMember()
                    }
                }

            }

            given("deleteBoard 메서드를 호출할 때") {
                every { boardRepository.findById(createTestBoard().id!!) } returns Optional.of(createTestBoard())
                every { boardRepository.deleteById(createTestBoard().id!!) } returns Unit

                `when`("삭제하려는 회원이 해당 게시글의 작성자라면") {
                    boardCommandService.deleteBoard(createTestBoard().id!!, REQUEST_MEMBER_ID)

                    then("게시글이 삭제되어야 한다.") {
                        io.mockk.verify { boardRepository.deleteById(createTestBoard().id!!) }
                    }
                }
            }

            given("updateBoard 메서드를 호출할 때") {
                every { boardRepository.findById(createTestBoard().id!!) } returns Optional.of(createTestBoard())
                every { boardRepository.deleteById(createTestBoard().id!!) } returns Unit

                `when`("수정하려는 회원이 해당 게시글의 작성자라면") {
                    val updatedContent = "Updated content"
                    val updatedDto = boardFormDto.copy(content = updatedContent)
                    createTestBoard().content = updatedContent

                    val result = boardCommandService.updateBoard(createTestBoard().id!!, updatedDto, REQUEST_MEMBER_ID)

                    then("게시글의 내용이 수정되어야 한다.") {
                        result.content shouldBe updatedContent
                    }
                }
            }
        })