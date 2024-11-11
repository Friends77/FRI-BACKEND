package com.friends.security.service

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class LoginResponseDto(
    val memberId: Long,
    val accessToken: String,
)

data class AtRtDto(
    val accessToken: String,
    val refreshToken: String,
)
