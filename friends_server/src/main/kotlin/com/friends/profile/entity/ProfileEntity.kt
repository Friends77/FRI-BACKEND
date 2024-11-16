package com.friends.profile.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.*
import java.util.*

@Entity
class Profile(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    val id: Long = 0L,

    //연관관계 미구현
    @OneToOne
    @JoinColumn(name = "member_id")
    val member: Member,

    var birth: Date,

    @Enumerated(EnumType.STRING)
    var gender: ProfileEnum?,

    var location: String?,

    @Column(name = "self_description", length = 50)
    var selfDescription: String?,

    @Column(name = "target_description", length = 50)
    var targetDescription: String?,

    @Enumerated(EnumType.STRING)
    var mbti: MbtiEnum?,

    @Column(name = "interest_tag", length = 20)
    var interestTag: String?,

    @Column(name = "hobby_tag", length = 20)
    var hobbyTag: String?,

    ) : BaseModifiableEntity() {

}