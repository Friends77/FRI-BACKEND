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
    val authToken: String,
    val email: String,
    val password: String,
    val nickname: String,
)

data class AtRtDto(
    val accessToken: String,
    val refreshToken: String,
)

data class OAuth2LoginDto(
    val isRegistered: Boolean,
    val memberId: Long? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val email: String? = null,
    val nickname: String? = null,
    val imageUrl: String? = null,
    val authToken: String? = null,
)

data class OAuth2LoginRequestDto(
    val code: String,
    val provider: OAuth2Provider,
)

data class OAuth2LoginResponseDto(
    val isRegistered: Boolean,
    val memberId: Long? = null,
    val accessToken: String? = null,
    val email: String? = null,
    val nickname: String? = null,
    val imageUrl: String? = null,
)

data class RefreshResponseDto(
    val accessToken: String,
)

data class LogoutRequestDto(
    val accessToken: String?,
)

data class CheckNicknameResponseDto(
    val isValid: Boolean,
    val message: String,
)

data class CheckEmailResponseDto(
    val isValid: Boolean,
    val message: String,
)

data class PasswordResetRequestDto(
    val emailAuthToken: String,
    val newPassword: String,
)
