package com.friends.security.service

import com.friends.common.exception.ErrorCode
import com.friends.jwt.AuthJwtRepository
import com.friends.jwt.JwtService
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.security.securityException.InvalidJwtException
import com.friends.security.userDetails.CustomUserDetails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val authJwtRepository: AuthJwtRepository,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun login(
        email: String,
        password: String,
    ): AtRtDto {
        val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        val userDetails = authenticate.principal as CustomUserDetails
        val accessToken = jwtService.createAccessToken(userDetails.memberId, userDetails.authorities)
        val refreshToken = jwtService.createRefreshToken(userDetails.memberId, userDetails.authorities)
        authJwtRepository.save(accessToken, refreshToken)
        return AtRtDto(accessToken, refreshToken)
    }

    fun refresh(refreshToken: String): AtRtDto {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw InvalidJwtException(ErrorCode.INVALID_TOKEN)
        }

        // refresh 될 때 기존의 access token 과 refresh token 을 삭제합니다.
        authJwtRepository.deleteRefreshToken(refreshToken)
        authJwtRepository.getAccessToken(refreshToken)?.apply {
            authJwtRepository.deleteAccessToken(this)
        }

        val memberId = jwtService.getMemberId(refreshToken)
        val authorities = jwtService.getAuthorities(refreshToken)

        val newAccessToken = jwtService.createAccessToken(memberId, authorities)
        val newRefreshToken = jwtService.createRefreshToken(memberId, authorities)
        authJwtRepository.save(newAccessToken, newRefreshToken)

        return AtRtDto(newAccessToken, newRefreshToken)
    }

    fun register(
        emailAuthToken: String,
        email: String,
        password: String,
        name: String,
    ) {
        val user =
            Member.createUser(
                name = name,
                email = email,
                password = passwordEncoder.encode(password),
            )
        memberRepository.save(user)
    }
}
