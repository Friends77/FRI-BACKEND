package com.friends.category.repository

import com.friends.category.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>
