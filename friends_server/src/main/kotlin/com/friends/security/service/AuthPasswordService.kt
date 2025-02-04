package com.friends.security.service

import com.friends.jwt.JwtService
import com.friends.member.repository.MemberRepository
import com.friends.security.securityException.EmailNotFoundException
import com.friends.security.securityException.InvalidTokenException
import com.friends.security.securityException.OAuth2ResetPasswordException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthPasswordService(
    private val jwtService: JwtService,
    private val memberRepository: MemberRepository,
    private val authValidator: AuthValidator,
) {
    @Transactional
    fun resetPassword(
        emailAuthToken: String,
        newPassword: String,
    ) {
        // emailAuthToken 검증
        if (!jwtService.validate(emailAuthToken)) {
            throw InvalidTokenException()
        }
        val emailFromToken = jwtService.getClaim(emailAuthToken, "email", String::class.java) ?: throw InvalidTokenException()

        // 유저 존재 여부 확인
        val member = memberRepository.findByEmail(emailFromToken) ?: throw EmailNotFoundException()

        // OAuth2 회원의 경우 패스워드 변경 불가
        val encryptedOldPassword = member.getPassword() ?: throw OAuth2ResetPasswordException()

        // 패스워드 유효성 검사
        authValidator.validateResetPassword(encryptedOldPassword, newPassword)

        // 패스워드 변경
        member.updatePassword(newPassword)
    }
}
