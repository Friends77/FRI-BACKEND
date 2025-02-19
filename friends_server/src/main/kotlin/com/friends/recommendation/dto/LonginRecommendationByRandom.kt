package com.friends.recommendation.dto

import com.friends.friendship.entity.FriendshipRequestStatusEnums
import com.friends.profile.dto.ProfileSimpleResponseDto

data class LonginRecommendationByRandom(
    val profileSimpleResponseDto: ProfileSimpleResponseDto,
    val type: FriendshipRequestStatusEnums,
)
