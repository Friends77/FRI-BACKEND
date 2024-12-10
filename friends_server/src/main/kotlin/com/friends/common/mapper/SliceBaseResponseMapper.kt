package com.friends.common.mapper

import com.friends.common.dto.SliceBaseResponse
import org.springframework.data.domain.Slice

fun <T> toSliceBaseResponse(slice: Slice<T>): SliceBaseResponse<T> = SliceBaseResponse(content = slice.content, hasNext = slice.hasNext())
