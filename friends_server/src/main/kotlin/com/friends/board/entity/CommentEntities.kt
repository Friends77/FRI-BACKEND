package com.friends.board.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Comment (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long = 0L,
    @Column(nullable = false)
    var comment: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_ic", nullable = false)
    val board: Board,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
) : BaseModifiableEntity(){
    fun updateComment(comment: String) {
        this.comment = comment
    }
}