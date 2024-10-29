package com.friends

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FriendsServerApplication

fun main(args: Array<String>) {
    runApplication<FriendsServerApplication>(*args)
}
