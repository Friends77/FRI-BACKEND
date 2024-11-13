package com.friends.board.entity

import com.friends.board.dto.BoardFormDto
import com.friends.member.entity.Member
import jakarta.persistence.*

@Entity
class BoardEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    val id: Long = 0L,

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    var content: String,

    @ElementCollection
    var hashtags: MutableSet<String> = mutableSetOf()
) {
    fun updateBoard(boardFormDto: BoardFormDto){
        content = boardFormDto.content
    }
}