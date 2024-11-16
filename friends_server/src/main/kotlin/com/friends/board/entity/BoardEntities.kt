package com.friends.board.entity

import com.friends.board.dto.BoardFormDto
import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.*
import org.hibernate.annotations.Fetch

@Entity
class Board (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    var content: String,

) : BaseModifiableEntity() {
    fun updateBoard(boardFormDto: BoardFormDto){
        content = boardFormDto.content
    }
}

@Entity
class BoardHashtag (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_hashtag_id")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    var board: Board,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    var hashtag: Hashtag,


)

@Entity
class Hashtag (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    val id: Long = 0L,

    var tag: String,

    @OneToMany(mappedBy = "hashtag", cascade =  [CascadeType.ALL], orphanRemoval = true)
    var boardHashtags: MutableSet<Hashtag> = mutableSetOf(),

) {

}