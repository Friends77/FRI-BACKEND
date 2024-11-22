package com.friends

import com.friends.config.AuthProperties
import com.friends.config.EmailProperties
import com.friends.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
@EnableConfigurationProperties(JwtProperties::class, AuthProperties::class, EmailProperties::class)
class FriendsServerApplication

fun main(args: Array<String>) {
    runApplication<FriendsServerApplication>(*args)
}
