package com.friends.board.repository

import com.friends.board.entity.Hashtag
import org.springframework.data.jpa.repository.JpaRepository

interface HashtagRepository : JpaRepository<Hashtag, Long> {
    //tag로 조회
    fun findByTag(tag: String): Hashtag?
}
