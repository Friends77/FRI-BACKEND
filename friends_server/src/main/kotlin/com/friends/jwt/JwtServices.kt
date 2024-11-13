package com.friends.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Date

/**
 * JWT 설정 정보를 담고 있는 클래스입니다.
 * application.yml 파일의 jwt 설정을 바탕으로 생성됩니다.
 */
@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val secretKey: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
)

@Component
class JwtService(
    val jwtProperties: JwtProperties,
) {
    val key by lazy { Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray()) }

    private fun getDateAfterSeconds(seconds: Long) = Date(System.currentTimeMillis() + seconds * 1000)

    fun createAccessToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): String =
        Jwts
            .builder()
            .claim("memberId", memberId.toString())
            .claim("authorities", authorities.map { it.authority })
            .signWith(key)
            .expiration(getDateAfterSeconds(jwtProperties.accessTokenExpiration))
            .compact()

    fun createRefreshToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): String =
        Jwts
            .builder()
            .claim("memberId", memberId.toString())
            .claim("authorities", authorities.map { it.authority })
            .signWith(key)
            .expiration(getDateAfterSeconds(jwtProperties.refreshTokenExpiration))
            .compact()

    fun getMemberId(token: String): Long =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .get("memberId", String::class.java)
            .toLong()

    fun getAuthorities(token: String): Collection<GrantedAuthority> {
        val result = mutableListOf<SimpleGrantedAuthority>()
        val authorities =
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
                .get("authorities", List::class.java)
        authorities.forEach { result.add(SimpleGrantedAuthority(it.toString())) }
        return result
    }

    fun getExpiration(token: String): Date =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload.expiration

    fun validate(token: String): Boolean =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
}
