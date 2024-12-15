package com.friends.board

import com.friends.board.dto.BoardAddDto
import com.friends.board.entity.Board
import com.friends.board.entity.BoardHashtag
import com.friends.board.entity.Hashtag
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

val REQUEST_MEMBER_ID = 1L
val NON_AUTHORIZED_MEMBER_ID = 2L
val BOARD_ID = 1L
val INVALID_BOARD_ID = 99L
val BOARD_HASHTAG_ID = 1L
val HASHTAG_ID = 1L
val PAGEABLE = PageRequest.of(0, 10, Sort.by("id").ascending())

fun createTestMember(): Member = Member(id = REQUEST_MEMBER_ID, nickname = "Test Name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE, imageUrl = "test imageurl")

fun createTestBoard(): Board = Board(id = BOARD_ID, member = createTestMember(), content = "Test content")

fun createTestHashtags(): Hashtag = Hashtag(id = HASHTAG_ID, tag = "testTag")

val testHashtags = listOf("friends", "kotlin")
val boardFormDto = BoardAddDto(content = createTestBoard().content, hashtags = testHashtags)

fun createBoardHashtag(): List<BoardHashtag> = listOf(BoardHashtag(id = BOARD_HASHTAG_ID, board = createTestBoard(), hashtag = createTestHashtags()))
