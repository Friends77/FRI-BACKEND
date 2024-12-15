package com.friends.chat.dto.category

import com.friends.common.entity.CategoryType
import io.swagger.v3.oas.annotations.media.Schema

data class CategoryInfoResponse(
    @Schema(description = "카테고리 ID")
    val id: Long,
    @Schema(description = "카테고리 이름")
    val name: String,
    @Schema(description = "카테고리 타입")
    val type: CategoryType,
)
