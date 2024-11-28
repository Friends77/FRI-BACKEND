package com.friends.common.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
abstract class BaseMongoTimeEntity {
    @CreatedDate
    lateinit var createdAt: LocalDateTime
        protected set
}
