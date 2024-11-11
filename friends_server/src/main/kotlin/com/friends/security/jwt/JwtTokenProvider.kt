package com.friends.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.MILLIS
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider : JwtInterface {
    @Value("\${jwt.secret}")
    private lateinit var secretKey: String
    val accessExpirationTime: Long = 1000 * 60 * 60 * 2 // 2시간
    val refreshExpirationTime: Long = 1000 * 60 * 60 * 24 * 7 // 7일

    val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }

    val log: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    fun createToken(
        claim: Map<String, Any>,
        expirationTime: Long = accessExpirationTime,
    ): String =
        Jwts
            .builder()
            .claims(claim) // 토큰에 담을 정보
            .expiration(Date.from(ZonedDateTime.now().plus(expirationTime, MILLIS).toInstant()))
            .signWith(key) // 기본 signature는 HS256
            .compact()

    override fun createAccessToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
    ): String =
        createToken(
            mapOf(
                "memberId" to memberId.toString(),
                "authorities" to authorities.map { it.authority },
            ),
            accessExpirationTime,
        )

    override fun createRefreshToken(
        memberId: Long,
        authorities: Collection<GrantedAuthority>,
        rotateId: String,
    ): String =
        createToken(
            mapOf(
                "memberId" to memberId.toString(),
                "rotateId" to rotateId,
                "authorities" to authorities.map { it.authority },
            ),
            refreshExpirationTime,
        )

    fun createRotateId(): String = UUID.randomUUID().toString()

    override fun getMemberId(token: String): Long =
        parseClaims(key, token).get("memberId", String::class.java).toLong()

    override fun getAuthorities(token: String): Collection<GrantedAuthority> =
        parseClaims(key, token)
            .get("authorities", List::class.java)
            .map { GrantedAuthority { it.toString() } }

    override fun validateToken(token: String): Boolean {
        try {
            val expirationTime = parseClaims(key, token).expiration
            return expirationTime.after(Date.from(ZonedDateTime.now().toInstant()))
        } catch (ex: ExpiredJwtException) {
            log.error("Jwt Expired: $token") // JWT 만료
        } catch (ex: JwtException) {
            log.error("Jwt Exception: $token") // JWT 예외
        }
        return false
    }

    override fun validateCacheRefreshToken(token: String): Boolean {
        TODO("Not yet implemented")
    }

    private fun parseClaims(
        key: SecretKey,
        token: String,
    ) = Jwts
        .parser()
        .verifyWith(key) // 서명 확인
        .decryptWith(key) // 복호화
        .build()
        .parseSignedClaims(token)
        .payload // 토큰 중간 부분으로 사용자 정보를 포함하고 있는 곳
}
