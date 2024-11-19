package com.friends.security.service

import com.friends.common.exception.ErrorCode
import com.friends.jwt.AtRtService
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
    private val atRtService: AtRtService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun login(
        email: String,
        password: String,
    ): AtRtDto {
        val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        val userDetails = authenticate.principal as CustomUserDetails
        return atRtService.createAtRt(userDetails.memberId, userDetails.authorities)
    }

    fun refresh(refreshToken: String): AtRtDto {
        if (!atRtService.validateRefreshToken(refreshToken)) {
            throw InvalidJwtException(ErrorCode.INVALID_TOKEN)
        }

        // refresh 될 때 기존의 access token 과 refresh token 을 삭제합니다.
        atRtService.deleteRefreshToken(refreshToken)
        atRtService.getAccessToken(refreshToken)?.apply {
            atRtService.deleteAccessToken(this)
        }

        val memberId = atRtService.getMemberId(refreshToken)
        val authorities = atRtService.getAuthorities(refreshToken)
        return atRtService.createAtRt(memberId, authorities)
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
