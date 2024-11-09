package com.friends.oauth2.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/auth")
class OAuth2Controller (
    private val webClient: WebClient.Builder
) {

    @Value("\${google.client-id}")
    private lateinit var clientId: String

    @Value("\${google.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${google.redirect-uri}")
    private lateinit var redirectUri: String

    /*
    * 0. 인가코드 발급 후 액세스 토큰 요청
    */

    @PostMapping("/oauth2-token")
    fun requestAccessToken(@RequestParam code: String): Mono<ResponseEntity<String>>{

        //client가 넘겨준 인가코드
        println("인가코드: $code")

        val tokenUri = "https://oauth2.googleapis.com/token"

        //access token 발급 요청 생성
        return webClient.build()
            .post()
            .uri(tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                "code=$code&client_id=$clientId&client_secret=$clientSecret&redirect_uri=$redirectUri&grant_type=authorization_code"
            )
            .retrieve()
            .bodyToMono(String::class.java)
            .map { tokenResponse ->
                println("액세스 토큰 요청 성공: $tokenResponse")
                ResponseEntity.ok("Access Token Response: $tokenResponse")
            }
            .onErrorResume { e ->
                println("액세스 토큰 요청 실패: ${e.message}")
                Mono.just(ResponseEntity.badRequest().body("Failed to get token"))
            }
    }

    /*
    * 1. accessToken을 통한 user data 넘겨받기
    */
    @GetMapping("/oauth2-data")
    fun getUserData(@RequestParam accessToken: String): Mono<ResponseEntity<String>> {
        val userInfoUri = "https://www.googleapis.com/oauth2/v2/userinfo"

        return webClient.build()
            .get()
            .uri(userInfoUri) { uriBuilder ->
                uriBuilder.queryParam("access_token", accessToken).build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .map { userDataResponse ->
                println("사용자 데이터 요청 성공: $userDataResponse")
                ResponseEntity.ok("User data Response: $userDataResponse")
            }
            .onErrorResume { e ->
                println("사용자 데이터 요청 실패: ${e.message}")
                Mono.just(ResponseEntity.status(500).body("Failed to get user data"))
            }

    }
}

