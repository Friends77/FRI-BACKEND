package com.friends.security.service

import com.friends.jwt.AtRtService
import com.friends.jwt.JwtService
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.member.repository.MemberRepository
import com.friends.oauth2.OAuth2Service
import com.friends.security.AtRtDto
import com.friends.security.OAuth2LoginSuccessDto
import com.friends.security.securityException.EmailDuplicateException
import com.friends.security.securityException.EmailNotFoundException
import com.friends.security.securityException.InvalidRefreshTokenException
import com.friends.security.securityException.InvalidTokenException
import com.friends.security.userDetails.CustomUserDetails
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val atRtService: AtRtService,
    private val jwtService: JwtService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val oAuth2Service: OAuth2Service,
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

    @Transactional
    fun register(
        emailAuthToken: String,
        email: String,
        password: String,
        name: String,
    ) {
        // emailAuthToken 검증
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

    @Transactional
    fun loginByOAuth2(
        code: String,
        oAuth2Provider: OAuth2Provider,
    ): OAuth2LoginSuccessDto {
        val userProfile = oAuth2Service.getUserProfile(code, oAuth2Provider)

        var firstLogin = false

        // 이미 가입되어 있다면 유저를 불러오고 없다면 새로 생성합니다.
        var user = memberRepository.findByEmail(userProfile.email)
        if (user == null) {
            user =
                Member.createUser(
                    name = userProfile.name,
                    email = userProfile.email,
                    oauth2Provider = oAuth2Provider,
                    imageUrl = userProfile.imageUrl,
                )
            memberRepository.save(user)
            firstLogin = true
        }

        // 이미 가입된 이메일의 소셜 서비스가 요청된 소셜 서비스와 다르다면 예외를 발생시킵니다.
        if (user.oauth2Provider != oAuth2Provider) {
            throw EmailDuplicateException()
        }

        val atRtDto = atRtService.createAtRt(user.id, user.authorities.map { SimpleGrantedAuthority(it.role.name) })
        return OAuth2LoginSuccessDto(firstLogin, atRtDto.accessToken, atRtDto.refreshToken)
    }

    fun logout(
        accessToken: String,
        refreshToken: String,
    ) {
        atRtService.deleteAccessToken(accessToken)
        atRtService.deleteRefreshToken(refreshToken)
    }

    @Transactional
    fun resetPassword(
        emailAuthToken: String,
        newPassword: String,
    ) {
        // emailAuthToken 검증
        if (!jwtService.validate(emailAuthToken)) {
            throw InvalidTokenException()
        }

        // jwt 에서 email 을 추출하고 해당 email 을 가진 사용자의 비밀번호를 변경합니다.
        val emailFromToken = jwtService.getClaim(emailAuthToken, "email", String::class.java) ?: throw InvalidTokenException()
        memberRepository.findByEmail(emailFromToken)?.apply {
            password = passwordEncoder.encode(newPassword)
            memberRepository.save(this)
        } ?: throw EmailNotFoundException()
    }
}
