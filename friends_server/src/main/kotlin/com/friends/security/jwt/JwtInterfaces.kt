package com.friends.security.jwt

import org.springframework.security.core.GrantedAuthority

abstract class JwtClaimReader {
    companion object {
        fun memberId(token: String): Long {
            TODO("Not yet implemented")
        }

        fun authorities(token: String): Collection<GrantedAuthority> {
            TODO("Not yet implemented")
        }
    }
}

abstract class JwtCreater {
    companion object {
        fun createAccessToken(
            memberId: Long,
            authorities: Collection<GrantedAuthority>,
        ): String {
            TODO("Not yet implemented")
        }

        fun createRefreshToken(
            memberId: Long,
            authorities: Collection<GrantedAuthority>,
        ): String {
            TODO("Not yet implemented")
        }
    }
}

abstract class JwtValidator {
    companion object {
        fun accessToken(token: String): Boolean {
            TODO("Not yet implemented")
        }

        fun refreshToken(token: String): Boolean {
            TODO("Not yet implemented")
        }
    }
}

class InvalidTokenException(s: String) : RuntimeException()
