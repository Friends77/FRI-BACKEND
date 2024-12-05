package com.friends.security.controller

import com.friends.jwt.AtRtService
import com.friends.security.LoginRequestDto
import com.friends.security.LoginResponseDto
import com.friends.security.LogoutRequestDto
import com.friends.security.OAuth2LoginRequestDto
import com.friends.security.OAuth2LoginResponseDto
import com.friends.security.RefreshResponseDto
import com.friends.security.RegisterRequestDto
import com.friends.security.service.AuthService
import org.springframework.http.HttpCookie
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
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
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.")
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
            // refresh token 을 쿠키로 전달합니다.
            .header(COOKIE_HEARER, getRefreshTokenCookie(atRtDto.refreshToken).toString())
            .body(LoginResponseDto(memberId, atRtDto.accessToken))
    }

    @PostMapping("/oauth2-login")
    fun oauth2Login(
        @RequestBody oauth2LoginRequestDto: OAuth2LoginRequestDto,
    ): ResponseEntity<OAuth2LoginResponseDto> {
        val oauth2LoginSuccessDto = authService.loginByOAuth2(oauth2LoginRequestDto.code, oauth2LoginRequestDto.provider)
        val memberId = atRtService.getMemberId(oauth2LoginSuccessDto.accessToken)

        return ResponseEntity
            .ok()
            // refresh token 을 쿠키로 전달합니다.
            .header(COOKIE_HEARER, getRefreshTokenCookie(oauth2LoginSuccessDto.refreshToken).toString())
            .body(OAuth2LoginResponseDto(memberId, oauth2LoginSuccessDto.accessToken, oauth2LoginSuccessDto.firstLogin))
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue refreshToken: String,
    ): ResponseEntity<RefreshResponseDto> {
        val atRtDto = authService.refresh(refreshToken)

        return ResponseEntity
            .ok()
            // refresh token 을 쿠키로 전달합니다.
            .header(COOKIE_HEARER, getRefreshTokenCookie(atRtDto.refreshToken).toString())
            .body(RefreshResponseDto(atRtDto.accessToken))
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody logoutRequestDto: LogoutRequestDto,
        @CookieValue refreshToken: String,
    ): ResponseEntity<String> {
        // accessToken 과 refreshToken 을 삭제합니다.
        authService.logout(logoutRequestDto.accessToken, refreshToken)
        return ResponseEntity
            .ok()
            .header(COOKIE_HEARER, getExpiredCookie().toString())
            .body("로그아웃이 완료되었습니다.")
    }

    /**
     * cookie 를 생성하여 문자열로 변환시 아래와 같은 형태로 변환됩니다.
     * "refreshToken=abc123; Max-Age=3600; Path=/; HttpOnly"
     */
    private fun getRefreshTokenCookie(refreshToken: String): HttpCookie {
        val expiration = atRtService.getExpiration(refreshToken)
        val expirationFromNowInSeconds = (expiration.time - System.currentTimeMillis()) / 1000
        return ResponseCookie
            .from("refreshToken", refreshToken)
            .httpOnly(true) // JavaScript 에서 쿠키에 접근할 수 없도록 하는 보안 설정입니다.
            .maxAge(expirationFromNowInSeconds) // 쿠키의 만료 시간을 설정합니다.
//            .secure(true) // cookie 가 https 에서만 전송되도록 하는 보안 설정입니다.
            .path("/api/auth") // 쿠키의 유효 범위를 설정합니다. (브라우저가 서버에 쿠키를 자동으로 전달하는 경로를 의미합니다.)
            .build()
    }

    private fun getExpiredCookie(): HttpCookie =
        ResponseCookie
            .from("refreshToken", "")
            .httpOnly(true)
            .maxAge(0)
            .path("/api/auth")
            .build()
}
