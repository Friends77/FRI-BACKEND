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

    //일단 set으로 구현, 추후 hashtag entity 설계 시 연관관계 구현예정
    @ElementCollection
    var hashtags: MutableSet<String> = mutableSetOf()
) {
    fun updateBoard(boardFormDto: BoardFormDto){
        content = boardFormDto.content
    }
}