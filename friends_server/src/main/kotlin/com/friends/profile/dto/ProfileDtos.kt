package com.friends.profile.dto

import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import java.time.LocalDate

//작성용 dto
data class ProfileCreateDto(
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: String? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: MutableSet<String> = mutableSetOf(),
    var imageUrl: String,
)

//수정가능한 필드 dto
data class ProfileUpdateDto(
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: String? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: MutableSet<String> = mutableSetOf(),
    var imageUrl: String,
)

//조회용 dto
data class ProfileResponseDto(
    val nickname: String,
    val email: String,
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: String?,
    var selfDescription: String?,
    var mbti: MbtiEnum?,
    var interestTag: MutableSet<String> = mutableSetOf(),
    var imageUrl: String,
)
