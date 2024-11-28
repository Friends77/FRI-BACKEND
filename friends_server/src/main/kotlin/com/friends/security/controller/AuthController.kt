package com.friends.security.controller

import com.friends.jwt.AtRtService
import com.friends.security.LoginRequestDto
import com.friends.security.LoginResponseDto
import com.friends.security.RegisterRequestDto
import com.friends.security.service.AuthService
import org.springframework.http.HttpCookie
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

const val COOKIE_HEARER = "Set-Cookie"

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val atRtService: AtRtService,
) {
    @PostMapping("/register")
    fun register(
        @RequestBody registerRequestDto: RegisterRequestDto,
    ): ResponseEntity<String> {
        authService.register(
            registerRequestDto.emailAuthToken,
            registerRequestDto.email,
            registerRequestDto.password,
            registerRequestDto.nickname,
        )
        return ResponseEntity.ok("회원가입이 완료되었습니다.")
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequestDto: LoginRequestDto,
    ): ResponseEntity<LoginResponseDto> {
        val atRtDto =
            authService.login(
                loginRequestDto.email,
                loginRequestDto.password,
            )
        val memberId = atRtService.getMemberId(atRtDto.accessToken)

        return ResponseEntity
            .ok()
            .header(COOKIE_HEARER, getRefreshTokenCookie(atRtDto.refreshToken).toString())
            .body(LoginResponseDto(memberId, atRtDto.accessToken))
    }

    private fun getRefreshTokenCookie(refreshToken: String): HttpCookie {
        val expiration = atRtService.getExpiration(refreshToken)
        val expirationFromNowInSeconds = (expiration.time - System.currentTimeMillis()) / 1000
        return ResponseCookie
            .from("refreshToken", refreshToken)
            .httpOnly(true)
            .maxAge(expirationFromNowInSeconds)
//            .secure(true) // https 에서 적용
            .path("/")
            .build()
    }
}
