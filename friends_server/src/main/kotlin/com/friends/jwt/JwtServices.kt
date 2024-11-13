package com.friends.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

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
