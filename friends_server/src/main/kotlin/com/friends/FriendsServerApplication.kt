package com.friends

import com.friends.jwt.AuthProperties
import com.friends.jwt.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class, AuthProperties::class)
class FriendsServerApplication

fun main(args: Array<String>) {
    runApplication<FriendsServerApplication>(*args)
}
