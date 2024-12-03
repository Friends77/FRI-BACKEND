package com.friends.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "chat_subject_category")
class ChatSubjectCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_subject_category_id")
    val id: Long,
    @Column(nullable = false, length = 30)
    val name: String,
)
