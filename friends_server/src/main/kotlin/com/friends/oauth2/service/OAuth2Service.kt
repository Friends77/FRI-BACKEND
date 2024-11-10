package com.friends.oauth2.service

import com.friends.config.OAuthConfig
import com.friends.security.entity.OAuth2Provider
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class OAuth2Service (
    private val webClient: WebClient.Builder
){

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
    * access Token 발급 요청
    * */
    fun requestAccessToken(code: String, provider: OAuth2Provider): Mono<ResponseEntity<String>>{
        val config = when(provider){
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
    유저 데이터 요청
    */
    fun getUserData(accessToken: String, provider: OAuth2Provider): Mono<ResponseEntity<String>>{
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