package com.friends.security.service

import com.friends.category.CategoryNotFoundException
import com.friends.jwt.JwtService
import com.friends.jwt.JwtType
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.profile.entity.Location
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.security.RegisterRequestDto
import com.friends.security.securityException.EmailDuplicateException
import com.friends.security.securityException.InvalidNicknameException
import com.friends.security.securityException.InvalidPasswordException
import com.friends.security.securityException.InvalidTokenException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

class AuthRegisterService(
    private val jwtService: JwtService,
) {
    @Transactional
    fun register(
        registerRequestDto: RegisterRequestDto,
        profileImage: MultipartFile?,
    ) {
        // emailAuthToken 검증
        if (!jwtService.validate(registerRequestDto.authToken)) {
            throw InvalidTokenException()
        }

        // Email-Password 회원가입인지, OAuth2 회원가입인지 확인
        val type = jwtService.getClaim(registerRequestDto.authToken, "type", String::class.java)?.let { JwtType.valueOf(it) } ?: throw InvalidTokenException()
        val user =
            when (type) {
                JwtType.EMAIL -> registerByEmail(registerRequestDto)
                JwtType.OAUTH2 -> registerByOAuth2(registerRequestDto)
            }

        // 이메일이 중복되는지 검증
        if (!validateEmail(user.email).isValid) {
            throw EmailDuplicateException()
        }

        // 닉네임이 중복되는지 검증
        if (!validateNickname(user.nickname).isValid) {
            throw InvalidNicknameException()
        }

        // 유효성 검사를 통과한 멤버 저장
        memberRepository.save(user)

        // 프로필 생성
        val profile =
            Profile(
                member = user,
                imageUrl = profileImage?.let { s3ClientService.upload(it) },
                gender = registerRequestDto.gender,
                birth = registerRequestDto.birth,
                location = registerRequestDto.location?.let { Location(it.latitude, it.longitude) },
                selfDescription = registerRequestDto.selfDescription,
                mbti = registerRequestDto.mbti,
            )
        profileRepository.save(profile)

        // 프로필 관심사 태그 생성
        profileInterestTagRepository.saveAll(
            registerRequestDto.interestTag.map {
                ProfileInterestTag(profile = profile, category = categoryRepository.findById(it).orElseThrow { CategoryNotFoundException() })
            },
        )
    }

    private fun registerByOAuth2(registerRequestDto: RegisterRequestDto): Member {
        val email = jwtService.getClaim(registerRequestDto.authToken, "email", String::class.java) ?: throw InvalidTokenException()
        val provider = jwtService.getClaim(registerRequestDto.authToken, "provider", String::class.java)?.let { OAuth2Provider.valueOf(it) } ?: throw InvalidTokenException()
        val nickname = registerRequestDto.nickname
        return Member.createUser(
            nickname = nickname,
            email = email,
            oauth2Provider = provider,
        )
    }

    private fun registerByEmail(registerRequestDto: RegisterRequestDto): Member {
        val email = jwtService.getClaim(registerRequestDto.authToken, "email", String::class.java) ?: throw InvalidTokenException()
        val nickname = registerRequestDto.nickname
        val password = registerRequestDto.password
        // 패스워드 유효성 검사
        if (!Member.validatePassword(password!!)) {
            throw InvalidPasswordException()
        }
        return Member.createUser(
            nickname = nickname,
            email = email,
            password = passwordEncoder.encode(password),
        )
    }
}
