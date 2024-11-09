package com.friends.auth.service

import com.friends.auth.dto.TokensResponse

interface AuthCommandServiceImpl {
    fun login(socialAccessToken: String): TokensResponse

    fun reissueTokens(refreshToken: String): TokensResponse
}
