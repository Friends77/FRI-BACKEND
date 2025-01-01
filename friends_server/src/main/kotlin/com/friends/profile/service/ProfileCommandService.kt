package com.friends.profile.service

import com.friends.category.repository.CategoryRepository
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfileCommandService(
    private val profileRepository: ProfileRepository,
    private val memberRepository: MemberRepository,
    private val categoryRepository: CategoryRepository,
    private val profileInterestTagRepository: ProfileInterestTagRepository,
) {
    //프로필 초기 작성
    fun createProfile(
        requestMemberId: Long,
        profileCreateDto: ProfileCreateDto,
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
                imageUrl = profileCreateDto.imageUrl,
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
    ) {
        val profile =
            profileRepository.findByMemberId(requestMemberId)
                ?: throw ProfileNullResponseException()
        profileInterestTagRepository.deleteByProfileId(profile.id)
        profileInterestTagRepository.saveAll(
            categoryRepository.findByIdIn(profileUpdateDto.interestTag).map {
                ProfileInterestTag(
                    profile = profile,
                    category = it,
                )
            },
        )
        profile.update(profileUpdateDto)
    }
}
