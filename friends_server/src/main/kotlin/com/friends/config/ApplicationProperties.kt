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

@ConfigurationProperties(prefix = "spring.mail")
class EmailProperties(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val auth: Boolean,
    val starttls: Boolean,
    val debug: Boolean,
    val connectiontimeout: Int,
)
