package com.friends.board

import com.friends.board.dto.BoardFormDto
import com.friends.board.entity.Board
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider

val REQUEST_MEMBER_ID = 1L
val BOARD_ID = 1L
val INVALID_BOARD_ID = 99L

fun createTestMember(): Member = Member(id = REQUEST_MEMBER_ID, name = "Test Name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE, imageUrl = "test imageurl")
fun createTestBoard(): Board = Board(id = BOARD_ID, member = createTestMember(), content = "Test content")

val testHashtags = listOf("friends", "kotlin")
val boardFormDto = BoardFormDto(content = createTestBoard().content, hashtags = testHashtags)
