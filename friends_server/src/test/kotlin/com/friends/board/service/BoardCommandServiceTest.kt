package com.friends.board.service

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.board.entity.BoardHashtag
import com.friends.board.entity.Hashtag
import com.friends.board.repository.BoardHashtagRepository
import com.friends.board.repository.BoardRepository
import com.friends.board.repository.HashtagRepository
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.member.repository.MemberRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

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

            val requestMemberId = 1L
            val member = Member(id = 1L, name = "Test Member", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE, imageUrl = "test imageurl")
            val mockBoard = Board(id = 100L, member = member, content = "Test content")
            val hashtagTags = listOf("friends", "kotlin")
            val boardFormDto = BoardFormDto(content = "Test content", hashtags = hashtagTags)

            isolationMode = IsolationMode.InstancePerLeaf

            //1. createBoard
            given("유효한 데이터로 게시글을 작성하는 경우") {
                every { memberRepository.findById(requestMemberId) } returns Optional.of(member)
                every { boardRepository.save(any()) } answers { firstArg() }
                every { hashtagRepository.findByTag(any()) } answers { null } //모든 해시태그는 새로 생성됩니다.
                every { hashtagRepository.save(any()) } answers { firstArg() } //저장된 해시태그 반환
                every { boardHashtagRepository.saveAll(any<List<BoardHashtag>>()) } answers { firstArg() }

                `when` ("createBoard 메서드를 호출하면") {
                    val result = boardCommandService.createBoard(boardFormDto, requestMemberId)

                    then("게시글이 저장되고 반환되어야 한다.") {
                        result.content shouldBe boardFormDto.content
                        result.member shouldBe member
                    }
                }

            }

            //2. deleteBoard
            given("작성자가 쓴 게시글이 존재할 때") {
                every { boardRepository.findById(mockBoard.id!!) } returns Optional.of(mockBoard)
                every { boardRepository.deleteById(mockBoard.id!!) } returns Unit

                `when`("deleteBoard 메서드를 호출하면") {
                    val result = boardCommandService.deleteBoard(mockBoard.id!!, requestMemberId)

                    then("게시글이 삭제되어야 한다.") {
                        io.mockk.verify { boardRepository.deleteById(mockBoard.id!!) }
                    }
                }
                //3. updateBoard
                `when`("updateBoard 메서드를 호출하면") {
                    val updatedContent = "Updated content"
                    val updatedDto = boardFormDto.copy(content = updatedContent)
                    mockBoard.content = updatedContent

                    val result = boardCommandService.updateBoard(mockBoard.id!!, updatedDto, requestMemberId)

                    then("게시글의 내용이 수정되어야 한다.") {
                        result.content shouldBe updatedContent
                    }
                }
            }
        })