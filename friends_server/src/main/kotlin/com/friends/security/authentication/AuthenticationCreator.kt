package com.friends.security.authentication

import com.friends.jwt.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AuthenticationCreator(
    private val jwtService: JwtService,
) {
    fun createByAccessToken(accessToken: String): Authentication {
        val memberId = jwtService.getMemberId(accessToken)
        val authorities = jwtService.getAuthorities(accessToken)
        return UsernamePasswordAuthenticationToken(memberId, accessToken, authorities)
    }
}
