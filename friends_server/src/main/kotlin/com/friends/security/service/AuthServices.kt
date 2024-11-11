package com.friends.security.service

import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.security.jwt.JwtInterface
import com.friends.security.userDetails.CustomUserDetails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtInterface: JwtInterface,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun login(
        email: String,
        password: String,
    ): AtRtDto {
        val authenticate = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        val userDetails = authenticate.principal as CustomUserDetails
        val accessToken = jwtInterface.createAccessToken(userDetails.memberId, userDetails.authorities)
        val refreshToken = jwtInterface.createRefreshToken(userDetails.memberId, userDetails.authorities)
        return AtRtDto(accessToken, refreshToken)
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
