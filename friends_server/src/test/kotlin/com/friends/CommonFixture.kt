package com.friends

import com.friends.chat.dto.category.CategoryInfoResponse
import com.friends.common.entity.Category
import com.friends.common.entity.CategoryType

const val TEST_SIZE = 10
const val TEST_CATEGORY_NAME = "일상"
const val TEST_CATEGORY_ID = 1L

fun createTestCategory(
    id: Long = TEST_CATEGORY_ID,
    name: String = TEST_CATEGORY_NAME,
    type: CategoryType = CategoryType.SUBJECT,
) = Category(id, name, type)

fun createTestCategoryInfoResponse(
    id: Long = TEST_CATEGORY_ID,
    name: String = TEST_CATEGORY_NAME,
    type: CategoryType = CategoryType.SUBJECT,
) = CategoryInfoResponse(id, name, type)
