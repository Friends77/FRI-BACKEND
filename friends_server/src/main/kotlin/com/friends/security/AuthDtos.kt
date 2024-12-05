package com.friends.security

import com.friends.member.entity.OAuth2Provider

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

data class OAuth2LoginSuccessDto(
    val firstLogin: Boolean,
    val accessToken: String,
    val refreshToken: String,
)

data class OAuth2LoginRequestDto(
    val code: String,
    val provider: OAuth2Provider,
)

data class OAuth2LoginResponseDto(
    val memberId: Long,
    val accessToken: String,
    val firstLogin: Boolean,
)

data class RefreshResponseDto(
    val accessToken: String,
)

data class LogoutRequestDto(
    val accessToken: String,
)

data class PasswordResetRequestDto(
    val emailAuthToken: String,
    val newPassword: String,
)
