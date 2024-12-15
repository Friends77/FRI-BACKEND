package com.friends.board.dto

import com.friends.chat.dto.category.CategoryInfoResponse

data class BoardRequestFormDto(
    var content: String,
    var categoryIds: Set<Long>,
)

data class BoardResponseFormDto(
    var content: String,
    var categories: List<CategoryInfoResponse>,
)
