package com.friends.auth.controller

import com.friends.auth.dto.TokensResponse
import com.friends.auth.service.AuthCommandService
import com.friends.security.securityException.MissingRefreshTokenException
import com.friends.security.securityException.MissingSocialAccessTokenException
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    val authCommandService: AuthCommandService,
) : AuthControllerSpec {

    @GetMapping
    override fun logIn(
        @RequestHeader(AUTHORIZATION) socialAccessToken: String?,
    ): TokensResponse {
        requireNotNull(socialAccessToken) { throw MissingSocialAccessTokenException() }
        return authCommandService.login(socialAccessToken)
    }

    @GetMapping("/refresh")
    override fun refresh(
        @RequestParam("token") refreshToken: String?,
    ): TokensResponse {
        requireNotNull(refreshToken) { throw MissingRefreshTokenException() }
        return authCommandService.reissueTokens(refreshToken)
    }
}
