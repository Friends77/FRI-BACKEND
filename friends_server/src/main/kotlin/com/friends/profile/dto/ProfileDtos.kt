package com.friends.profile.dto

import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import java.util.Date

//작성용 dto
data class ProfileCreateDto(
    var birth: Date,
    var gender: GenderEnum,
    var location: String? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: List<String>? = null,
    var imageUrl: String,
)

//수정가능한 필드 dto
data class ProfileUpdateDto(
    var birth: Date,
    var gender: GenderEnum,
    var location: String? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: List<String>? = null,
    var imageUrl: String,
)

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
