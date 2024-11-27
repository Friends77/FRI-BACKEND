package com.friends.profile.dto

import com.friends.member.entity.Member
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import java.util.Date

data class ProfileDto (
    var birth: Date,
    var gender: GenderEnum,
    var location: String? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: List<String>? = null,
    var imageUrl: String,
){
    fun toEntity(profileDto: ProfileDto, member: Member): Profile {
        return Profile.build(
            member = member,
            birth = profileDto.birth,
            gender = profileDto.gender,
            location = profileDto.location,
            selfDescription = profileDto.selfDescription,
            mbti = profileDto.mbti,
            interestTag = profileDto.interestTag?.toMutableSet() ?: mutableSetOf(),
            imageUrl = profileDto.imageUrl
        )
    }

}

