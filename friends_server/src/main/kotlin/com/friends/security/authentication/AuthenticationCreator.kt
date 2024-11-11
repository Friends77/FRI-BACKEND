package com.friends.security.authentication

import com.friends.security.jwt.JwtInterface
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AuthenticationCreator(
    private val jwtInterface: JwtInterface,
) {
    fun createByAccessToken(accessToken: String): Authentication {
        val memberId = jwtInterface.getMemberId(accessToken)
        val authorities = jwtInterface.getAuthorities(accessToken)
        return UsernamePasswordAuthenticationToken(memberId, accessToken, authorities)
    }
}
