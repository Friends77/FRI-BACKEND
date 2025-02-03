package com.friends.security.service

import com.friends.jwt.JwtService
import com.friends.member.repository.MemberRepository
import com.friends.security.securityException.DuplicateNewPasswordException
import com.friends.security.securityException.EmailNotFoundException
import com.friends.security.securityException.InvalidPasswordException
import com.friends.security.securityException.InvalidTokenException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthPasswordService(
    private val jwtService: JwtService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
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

        // 패스워드 유효성 검사
        // 적절한 비밀번호 패턴인지 검사
        if (!validatePassword(newPassword)) {
            throw InvalidPasswordException()
        }
        // 새 비밀번호와 기존 비밀번호가 같은지 검사
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw DuplicateNewPasswordException()
        }

        // jwt 에서 email 을 추출하고 해당 email 을 가진 사용자의 비밀번호를 변경합니다.
        member.updatePassword(passwordEncoder.encode(newPassword))
        memberRepository.save(member)
    }
}
