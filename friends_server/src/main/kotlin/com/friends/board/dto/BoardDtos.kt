package com.friends.board.dto

data class BoardAddDto(
    var content: String,
    var hashtags: List<String>,
)

data class BoardUpdateDto(
    var content: String,
    var hashtags: List<String>,
)

data class BoardResponseDto(
    var content: String,
    var hashtags: List<String>,
)