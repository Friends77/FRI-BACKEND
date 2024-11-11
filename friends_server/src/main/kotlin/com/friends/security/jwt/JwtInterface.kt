package com.friends.security.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component

@Component
interface JwtInterface {
    fun getMemberId(token: String): Long {
        TODO("Not yet implemented")
    }

    fun getAuthorities(token: String): Collection<GrantedAuthority> {
        TODO("Not yet implemented")
    }

    fun createAccessToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): String {
        TODO("Not yet implemented")
    }

    fun createRefreshToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
        rotateId: String,
    ): String {
        TODO("Not yet implemented")
    }

    fun validateAccessToken(token: String): Boolean {
        TODO("Not yet implemented")
    }

    fun validateRefreshToken(token: String): Boolean {
        TODO("Not yet implemented")
    }
}
