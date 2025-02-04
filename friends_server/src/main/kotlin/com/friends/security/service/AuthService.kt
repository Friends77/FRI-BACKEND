package com.friends.security.service

import com.friends.category.repository.CategoryRepository
import com.friends.config.AuthProperties
import com.friends.image.S3ClientService
import com.friends.jwt.AtRtService
import com.friends.jwt.JwtService
import com.friends.jwt.JwtType
import com.friends.member.entity.OAuth2Provider
import com.friends.member.repository.MemberRepository
import com.friends.oauth2.OAuth2Service
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import com.friends.security.AtRtDto
import com.friends.security.CheckEmailResponseDto
import com.friends.security.CheckNicknameResponseDto
import com.friends.security.OAuth2LoginDto
import com.friends.security.securityException.EmailDuplicateException
import com.friends.security.securityException.InvalidRefreshTokenException
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
    private val authProperties: AuthProperties,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val oAuth2Service: OAuth2Service,
    private val profileInterestTagRepository: ProfileInterestTagRepository,
    private val categoryRepository: CategoryRepository,
    private val profileRepository: ProfileRepository,
    private val s3ClientService: S3ClientService,
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

    fun validateEmail(email: String): CheckEmailResponseDto =
        if (memberRepository.existsByEmail(email)) {
            CheckEmailResponseDto(false, "이미 사용 중인 이메일입니다.")
        } else {
            CheckEmailResponseDto(true, "사용 가능한 이메일입니다.")
        }

    fun loginByOAuth2(
        code: String,
        oAuth2Provider: OAuth2Provider,
    ): OAuth2LoginDto {
        val userProfile = oAuth2Service.getUserProfile(code, oAuth2Provider)

        var user = memberRepository.findByEmail(userProfile.email)
        // 이미 가입된 사용자인 경우
        if (user != null) {
            if (user.oauth2Provider != oAuth2Provider) {
                throw EmailDuplicateException()
            }
            val atRtoDto = atRtService.createAtRt(user.id, user.authorities.map { SimpleGrantedAuthority(it.role.name) })
            return OAuth2LoginDto(isRegistered = true, memberId = user.id, accessToken = atRtoDto.accessToken, refreshToken = atRtoDto.refreshToken)
        } else { // 가입되지 않은 사용자인 경우
            val authToken =
                jwtService.createToken(
                    "email" to userProfile.email,
                    "type" to JwtType.OAUTH2,
                    "provider" to oAuth2Provider,
                    expirationSeconds = authProperties.oauth2JwtExpiration,
                )
            return OAuth2LoginDto(isRegistered = false, email = userProfile.email, nickname = userProfile.name, imageUrl = userProfile.imageUrl, authToken = authToken)
        }
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
}
