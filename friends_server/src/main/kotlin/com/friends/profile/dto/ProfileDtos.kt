package com.friends.profile.dto

import com.friends.category.entity.Category
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Location
import com.friends.profile.entity.MbtiEnum
import java.time.LocalDate

//작성용 dto
data class ProfileCreateDto(
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: Location? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: MutableSet<Long> = mutableSetOf(),
    var imageUrl: String,
)

//수정가능한 필드 dto
data class ProfileUpdateDto(
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: Location? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: MutableSet<Long> = mutableSetOf(),
    var imageUrl: String,
)

//조회용 dto
data class ProfileResponseDto(
    val nickname: String,
    val email: String,
    var birth: LocalDate,
    var gender: GenderEnum,
    var location: Location?,
    var selfDescription: String?,
    var mbti: MbtiEnum?,
    var interestTag: MutableSet<Category> = mutableSetOf(),
    var imageUrl: String,
)

data class ProfileWithDistanceDto(
    val id: Long,
    val nickname: String,
    val imageUrl: String,
    val distance: Double,
)

data class ProfileWithCategories(
    val id: Long,
    val nickname: String,
    val imageUrl: String,
    val categoryIds: List<Long>,
)
