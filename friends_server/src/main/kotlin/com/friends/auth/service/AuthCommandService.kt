package com.friends.auth.service

import com.friends.auth.dto.TokensResponse
import org.springframework.stereotype.Service

@Service
class AuthCommandService : AuthCommandServiceImpl {
    override fun login(socialAccessToken: String): TokensResponse {
        TODO("Not yet implemented")
    }

    override fun reissueTokens(refreshToken: String): TokensResponse {
        TODO("Not yet implemented")
    }
}
