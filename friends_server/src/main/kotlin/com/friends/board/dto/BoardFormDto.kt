package com.friends.board.dto

import com.friends.category.dto.CategoryInfoResponse

data class BoardRequestFormDto(
    var content: String,
    var categoryIds: Set<Long>,
)

data class BoardResponseFormDto(
    var content: String,
    var categories: List<CategoryInfoResponse>,
)
