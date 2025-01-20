package com.friends.profile.service

import com.friends.category.repository.CategoryRepository
import com.friends.image.S3ClientService
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import com.friends.security.service.AuthService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class ProfileCommandService(
    private val profileRepository: ProfileRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val profileInterestTagRepository: ProfileInterestTagRepository,
    private val s3ClientService: S3ClientService,
    private val authService: AuthService,
) {
    //프로필 초기 작성
    fun createProfile(
        requestMemberId: Long,
        profileCreateDto: ProfileCreateDto,
        profileImage: MultipartFile?,
    ) {
        val member =
            memberRepository
                .findById(requestMemberId)
                .orElseThrow { MemberNotFoundException() }

        val profileInterestTag = categoryRepository.findByIdIn(profileCreateDto.interestTag)
        val profile =
            Profile(
                birth = profileCreateDto.birth,
                gender = profileCreateDto.gender,
                location = profileCreateDto.location,
                selfDescription = profileCreateDto.selfDescription,
                mbti = profileCreateDto.mbti,
                imageUrl = profileImage?.let { s3ClientService.upload(it) },
                member = member,
            )
        val profileInterestTagList =
            profileInterestTag.map {
                ProfileInterestTag(
                    profile = profile,
                    category = it,
                )
            }
        profileRepository.save(profile)
        profileInterestTagRepository.saveAll(profileInterestTagList)
    }

    //프로필 수정
    fun updateProfile(
        requestMemberId: Long,
        profileUpdateDto: ProfileUpdateDto,
        profileImage: MultipartFile?,
    ) {
        val profile = profileRepository.findByMemberId(requestMemberId) ?: throw ProfileNullResponseException()

        val profileImageUrl = handleImageUpdate(profile, profileImage)
        handleInterestTagUpdate(profile, profileUpdateDto)
        handlerNicknameUpdate(profile, profileUpdateDto)
        profile.update(profileUpdateDto, profileImageUrl)
    }

    private fun handleImageUpdate(
        profile: Profile,
        profileImage: MultipartFile?,
    ): String? {
        profile.imageUrl?.let { s3ClientService.deleteS3Object(it) }
        return if (profileImage != null) {
            s3ClientService.upload(profileImage)
        } else {
            null
        }
    }

    private fun handleInterestTagUpdate(
        profile: Profile,
        profileUpdateDto: ProfileUpdateDto,
    ) {
        profileInterestTagRepository.deleteByProfileId(profile.id)
        profileInterestTagRepository.saveAll(
            categoryRepository.findByIdIn(profileUpdateDto.interestTag).map {
                ProfileInterestTag(
                    profile = profile,
                    category = it,
                )
            },
        )
    }

    private fun handlerNicknameUpdate(
        profile: Profile,
        profileUpdateDto: ProfileUpdateDto,
    ) {
        if (authService.validateNickname(profileUpdateDto.nickname).isValid) {
            profile.member.nickname = profileUpdateDto.nickname
        }
    }
}
