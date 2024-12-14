package com.friends.security.service

import com.friends.jwt.AtRtService
import com.friends.jwt.JwtService
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.member.repository.MemberRepository
import com.friends.oauth2.OAuth2Service
import com.friends.security.AtRtDto
import com.friends.security.CheckNicknameResponseDto
import com.friends.security.OAuth2LoginSuccessDto
import com.friends.security.securityException.EmailDuplicateException
import com.friends.security.securityException.EmailNotFoundException
import com.friends.security.securityException.InvalidNicknameException
import com.friends.security.securityException.InvalidPasswordException
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
        nickname: String,
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

        // 비밀번호 규칙 검증
        if (!validatePassword(password)) {
            throw InvalidPasswordException()
        }

        // 닉네임 유효성 검사
        if (!validateNickname(nickname).isValid) {
            throw InvalidNicknameException()
        }

        val user =
            Member.createUser(
                nickname = nickname,
                email = email,
                password = passwordEncoder.encode(password),
            )
        memberRepository.save(user)
    }

    fun validatePassword(password: String): Boolean {
        val lengthRegex = Regex(".{8,20}") // 길이 제한
        val lowerCaseRegex = Regex(".*[a-z].*") // 소문자 포함
        val digitRegex = Regex(".*[0-9].*") // 숫자 포함
        val specialCharRegex = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*") // 특수문자 포함
        val noWhiteSpaceRegex = Regex("^[^\\s]*\$") // 공백 금지

        return lengthRegex.matches(password) &&
            lowerCaseRegex.containsMatchIn(password) &&
            digitRegex.containsMatchIn(password) &&
            specialCharRegex.containsMatchIn(password) &&
            noWhiteSpaceRegex.matches(password)
    }

    fun validateNickname(nickname: String): CheckNicknameResponseDto {
        val lengthRegex = Regex("^[가-힣a-zA-Z0-9]{2,20}\$") // 한글, 숫자, 영문 포함 2~20자

        return if (memberRepository.existsByNickname(nickname)) {
            CheckNicknameResponseDto(false, "이미 사용 중인 닉네임입니다.")
        } else if (!lengthRegex.matches(nickname)) {
            CheckNicknameResponseDto(false, "2~20자의 한글, 영문, 숫자만 사용 가능합니다.")
        } else {
            CheckNicknameResponseDto(true, "사용 가능한 닉네임입니다.")
        }
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
                    nickname = userProfile.name,
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
        accessToken: String?,
        refreshToken: String?,
    ) {
        accessToken?.let { accessToken ->
            atRtService.getRefreshToken(accessToken)?.let { atRtService.deleteRefreshToken(it) }
            atRtService.deleteAccessToken(accessToken)
        }
        refreshToken?.let { refreshToken ->
            atRtService.getAccessToken(refreshToken)?.let { atRtService.deleteAccessToken(it) }
            atRtService.deleteRefreshToken(refreshToken)
        }
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
