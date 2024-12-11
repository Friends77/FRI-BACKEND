package com.friends.chat.repository

import com.friends.chat.entity.ChatSubjectCategory
import org.springframework.data.jpa.repository.JpaRepository

interface ChatSubjectCategoryRepository : JpaRepository<ChatSubjectCategory, Long> {
    fun findByIdIn(ids: Set<Long>): List<ChatSubjectCategory>
}
