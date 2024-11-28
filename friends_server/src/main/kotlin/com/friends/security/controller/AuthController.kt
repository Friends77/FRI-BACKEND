package com.friends.security.controller

import com.friends.security.RegisterRequestDto
import com.friends.security.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
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
}
