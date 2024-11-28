package com.friends.security.service

import com.friends.jwt.AtRtService
import com.friends.jwt.JwtService
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.security.AtRtDto
import com.friends.security.securityException.EmailDuplicateException
import com.friends.security.securityException.InvalidRefreshTokenException
import com.friends.security.securityException.InvalidTokenException
import com.friends.security.userDetails.CustomUserDetails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val atRtService: AtRtService,
    private val jwtService: JwtService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional(readOnly = true)
    fun login(
        email: String,
        password: String,
    ): AtRtDto {
        val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        val userDetails = authenticate.principal as CustomUserDetails
        return atRtService.createAtRt(userDetails.memberId, userDetails.authorities)
    }

    @Transactional(readOnly = true)
    fun refresh(refreshToken: String): AtRtDto {
        if (!atRtService.validateRefreshToken(refreshToken)) {
            throw InvalidRefreshTokenException()
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
//         emailAuthToken 검증
        if (!jwtService.validate(emailAuthToken)) {
            throw InvalidTokenException()
        }

        // emailAuthToken 에서 email 추출하여 인증 받은 이메일과 일치하는지 검증
        val emailFromToken = jwtService.getClaim(emailAuthToken, "email", String::class.java)
        if (emailFromToken != email) {
            throw InvalidTokenException()
        }

        // 이메일이 중복되는지 검증
        if (memberRepository.existsByEmail(email)) {
            throw EmailDuplicateException()
        }
        val user =
            Member.createUser(
                name = name,
                email = email,
                password = passwordEncoder.encode(password),
            )
        memberRepository.save(user)
    }
}
