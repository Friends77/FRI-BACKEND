package com.friends.common.mapper

import com.friends.chat.dto.category.CategoryInfoResponse
import com.friends.common.dto.SliceBaseResponse
import com.friends.common.entity.Category
import org.springframework.data.domain.Slice

fun <T> toSliceBaseResponse(slice: Slice<T>): SliceBaseResponse<T> = SliceBaseResponse(content = slice.content, hasNext = slice.hasNext())

fun toCategoryInfoResponse(category: Category) = CategoryInfoResponse(category.id, category.name, category.type)
