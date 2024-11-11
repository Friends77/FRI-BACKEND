package com.friends.security.jwt

import org.springframework.security.core.GrantedAuthority

interface JwtInterface {
    fun getMemberId(token: String): Long

    fun getAuthorities(token: String): Collection<GrantedAuthority>

    fun createAccessToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): String

    fun createRefreshToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
        rotateId: String,
    ): String

    fun validateToken(token: String): Boolean

    fun validateCacheRefreshToken(token: String): Boolean
}
