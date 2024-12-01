package com.friends.profile.dto

import com.friends.member.entity.Member
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import java.util.Date

//수정가능한 필드 dto
data class ProfileUpdateDto(
    var birth: Date,
    var gender: GenderEnum,
    var location: String? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: List<String>? = null,
    var imageUrl: String,
) {
    fun toEntity(
        profileUpdateDto: ProfileUpdateDto,
        member: Member,
    ): Profile {
        return Profile.build(
            member = member,
            birth = profileUpdateDto.birth,
            gender = profileUpdateDto.gender,
            location = profileUpdateDto.location,
            selfDescription = profileUpdateDto.selfDescription,
            mbti = profileUpdateDto.mbti,
            interestTag = profileUpdateDto.interestTag?.toMutableSet() ?: mutableSetOf(),
            imageUrl = profileUpdateDto.imageUrl,
        )
    }
}

//조회용 dto
data class ProfileResponseDto(
    val nickname: String,
    val email: String,
    var birth: Date,
    var gender: GenderEnum,
    var location: String?,
    var selfDescription: String?,
    var mbti: MbtiEnum?,
    var interestTag: List<String>?,
    var imageUrl: String,
)
