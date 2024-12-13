package com.friends.profile.entity

import com.friends.common.entity.BaseModifiableEntity
import com.friends.member.entity.Member
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.dto.ProfileUpdateDto
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.LocalDate

@Entity
class Profile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    val id: Long = 0L,
    //member 엔티티에서 닉네임만 받아옵니다
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
    var birth: LocalDate,
    @Enumerated(EnumType.STRING)
    var gender: GenderEnum,
    @Embedded
    var location: Location? = null,
    @Column(name = "self_description", length = 100)
    var selfDescription: String? = null,
    @Enumerated(EnumType.STRING)
    var mbti: MbtiEnum? = null,
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "interest_tag", length = 225)
    var interestTag: MutableSet<String> = mutableSetOf(),
    //기본이미지가 있기 때문에 null이 될 수 없습니다
    @Column(name = "image_url")
    var imageUrl: String,
) : BaseModifiableEntity() {
    @PrePersist
    @PreUpdate
    fun validate() {
        if (interestTag.isEmpty()) {
            throw IllegalArgumentException("관심사 태그는 최소 1개 이상 선택되어야 합니다.")
        }
    }

    fun update(
        profileUpdateDto: ProfileUpdateDto,
    ) {
        this.birth = profileUpdateDto.birth
        this.gender = profileUpdateDto.gender
        this.location = profileUpdateDto.location
        this.selfDescription = profileUpdateDto.selfDescription
        this.mbti = profileUpdateDto.mbti
        this.interestTag = profileUpdateDto.interestTag
        this.imageUrl = profileUpdateDto.imageUrl
    }

    fun toResponseDto(): ProfileResponseDto =
        ProfileResponseDto(
            nickname = this.member.nickname,
            email = this.member.email,
            birth = this.birth,
            gender = this.gender,
            location = this.location,
            selfDescription = this.selfDescription,
            mbti = this.mbti,
            interestTag = this.interestTag,
            imageUrl = this.imageUrl,
        )
}

@Embeddable
data class Location(
    val latitude: Double,
    val longitude: Double,
)
