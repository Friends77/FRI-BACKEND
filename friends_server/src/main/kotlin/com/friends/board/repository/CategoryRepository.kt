package com.friends.board.repository

import com.friends.common.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Category?

    fun findByIdIn(ids: Set<Long>): List<Category>
}
