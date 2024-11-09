package com.friends.oauth2.controller

import com.friends.security.entity.OAuth2Provider
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

data class OAuthConfig(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val tokenUri: String,
)

@RestController
@RequestMapping("/api/auth")
class OAuth2Controller (
    private val webClient: WebClient.Builder
) {

    @Value("\${google.client-id}")
    private lateinit var googleClientId: String

    @Value("\${google.client-secret}")
    private lateinit var googleClientSecret: String

    @Value("\${google.redirect-uri}")
    private lateinit var googleRedirectUri: String

    @Value("\${naver.client-id}")
    private lateinit var naverClientId: String

    @Value("\${naver.client-secret}")
    private lateinit var naverClientSecret: String

    @Value("\${naver.redirect-uri}")
    private lateinit var naverRedirectUri: String


    /*
    * 0. 인가코드 발급 후 액세스 토큰 요청
    */

    @PostMapping("/oauth2-token")
    fun requestAccessToken(@RequestParam code: String, @RequestParam provider: OAuth2Provider): Mono<ResponseEntity<String>>{

        println("인가코드: $code, 공급자: $provider")

        val config = when (provider) {
            OAuth2Provider.GOOGLE -> OAuthConfig(
                googleClientId,
                googleClientSecret,
                googleRedirectUri,
                "https://oauth2.googleapis.com/token"
            )

            OAuth2Provider.NAVER -> OAuthConfig(
                naverClientId,
                naverClientSecret,
                naverRedirectUri,
                "https://nid.naver.com/oauth2.0/token"
            )
        }

        return webClient.build()
            .post()
            .uri(config.tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                "code=$code&client_id=${config.clientId}&client_secret=${config.clientSecret}&redirect_uri=${config.redirectUri}&grant_type=authorization_code"
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
    fun getUserData(@RequestParam accessToken: String, @RequestParam provider: OAuth2Provider): Mono<ResponseEntity<String>> {

        val userInfoUri = when (provider) {
            OAuth2Provider.GOOGLE -> "https://www.googleapis.com/oauth2/v2/userinfo"
            OAuth2Provider.NAVER -> "https://openapi.naver.com/v1/nid/me"
        }

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

