package com.friends.security

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class LoginResponseDto(
    val memberId: Long,
    val accessToken: String,
)

data class RegisterRequestDto(
    val emailAuthToken: String,
    val email: String,
    val password: String,
    val nickname: String,
)

data class AtRtDto(
    val accessToken: String,
    val refreshToken: String,
)
