package com.friends.recommendation.controller

import com.friends.common.dto.ListBaseResponse
import com.friends.profile.dto.ProfileWithCategories
import com.friends.recommendation.service.GlobalRecommendationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/global/recommendation")
class GlobalRecommendationController(
    private val globalRecommendationService: GlobalRecommendationService,
) : GlobalRecommendationControllerSpec {
    @GetMapping("/category")
    override fun getCategoryRecommendation(
        @RequestParam categoryIds: List<Long>,
        @RequestParam(required = false, defaultValue = "20") size: Int,
    ): ResponseEntity<ListBaseResponse<ProfileWithCategories>> {
        val result = globalRecommendationService.getRecommendationByUserCategory(categoryIds, size)
        return ResponseEntity.ok(result)
    }
}
