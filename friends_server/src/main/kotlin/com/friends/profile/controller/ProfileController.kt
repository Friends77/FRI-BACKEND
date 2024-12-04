package com.friends.profile.controller

import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.service.ProfileCommandService
import com.friends.profile.service.ProfileQueryService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController (
    val profileCommandService: ProfileCommandService,
    val profileQueryService: ProfileQueryService
) {
    //프로필 조회
    @GetMapping("api/user/profile/{memberId}")
    fun getProfile(
        @PathVariable memberId: Long
    ): ResponseEntity<ProfileResponseDto>{
        val profile = profileQueryService.getProfile(memberId)
        return ResponseEntity.ok(profile)
    }

    //프로필 작성(초기화면)
    @PostMapping("api/user/profile")
    fun createProfile(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody @Valid profileCreateDto: ProfileCreateDto
    ): ResponseEntity<Void>{
        profileCommandService.createProfile(memberId, profileCreateDto)
        return ResponseEntity.noContent().build()
    }

    //프로필 수정
    @PutMapping("api/user/profile")
    fun updateProfile(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody @Valid profileUpdateDto: ProfileUpdateDto
    ): ResponseEntity<ProfileUpdateDto>{
        profileCommandService.updateProfile(memberId, profileUpdateDto)
        return ResponseEntity.ok(profileUpdateDto)
    }
}