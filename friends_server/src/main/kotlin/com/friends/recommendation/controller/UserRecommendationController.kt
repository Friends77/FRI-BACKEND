package com.friends.recommendation.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.profile.dto.ProfileWithDistanceDto
import com.friends.recommendation.service.UserRecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/global/recommendation")
class UserRecommendationController(
    private val userRecommendationService: UserRecommendationService,
) : UserRecommendationControllerSpec {
    @GetMapping("/distance")
    override fun getDistanceRecommendation(
        @AuthenticationPrincipal memberId: Long,
        @RequestParam distance: Double,
        @RequestParam size: Int,
    ): ResponseEntity<ListBaseResponse<ProfileWithDistanceDto>> {
        val result = userRecommendationService.getDistanceRecommendation(memberId, distance, size)
        return ResponseEntity.ok(result)
    }
}
