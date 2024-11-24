package com.friends.profile.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import java.util.Date
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.FetchType

@Entity
class Profile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    val id: Long = 0L,
    @OneToOne
    @JoinColumn(name = "member_id")
    val member: Member,
    var birth: Date,
    @Enumerated(EnumType.STRING)
    var gender: GenderEnum?,
    var location: String?,
    @Column(name = "self_description", length = 50)
    var selfDescription: String?,
    @Column(name = "target_description", length = 50)
    var targetDescription: String?,
    @Enumerated(EnumType.STRING)
    var mbti: MbtiEnum?,
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "interest_tag", length = 225)
    var interestTag: MutableSet<String> = mutableSetOf(),
    @Column(name = "hobby_tag", length = 225)
    var hobbyTag: String?,
) : BaseModifiableEntity()
