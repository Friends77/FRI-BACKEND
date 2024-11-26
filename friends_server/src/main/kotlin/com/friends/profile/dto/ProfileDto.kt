package com.friends.profile.dto

import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import java.util.Date

data class ProfileDto (
    val nickname: String,
    val email: String,
    var birth: Date? = null,
    var gender: GenderEnum? = null,
    var location: String? = null,
    var selfDescription: String? = null,
    var mbti: MbtiEnum? = null,
    var interestTag: List<String>? = null,
    var hobbyTag: List<String>? = null,
)