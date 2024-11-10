package com.friends.oauth2.controller

import com.friends.oauth2.service.OAuth2Service
import com.friends.security.entity.OAuth2Provider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/auth")
class OAuth2Controller(
    private val oAuth2Service: OAuth2Service
) {
    /*
    * 0. 인가코드 발급 후 액세스 토큰 요청
    */

    @PostMapping("/oauth2-token")
    fun requestAccessToken(@RequestParam code: String, @RequestParam provider: OAuth2Provider): Mono<ResponseEntity<String>>{
        println("인가코드: $code, 공급자: $provider")
        return oAuth2Service.requestAccessToken(code, provider)
    }

    /*
    * 1. accessToken을 통한 user data 넘겨받기
    */
    @GetMapping("/oauth2-data")
    fun getUserData(@RequestParam accessToken: String, @RequestParam provider: OAuth2Provider): Mono<ResponseEntity<String>> {
        return oAuth2Service.getUserData(accessToken, provider)
    }
}

