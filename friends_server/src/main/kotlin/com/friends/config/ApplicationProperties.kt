package com.friends.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth")
class AuthProperties(
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
)

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val secretKey: String,
)
