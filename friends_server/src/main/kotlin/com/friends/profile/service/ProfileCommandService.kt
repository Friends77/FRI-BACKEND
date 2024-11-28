package com.friends.profile.service

import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.profile.ProfileNullResponseException
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProfileCommandService (
    private val profileRepository: ProfileRepository,
    private val memberRepository: MemberRepository
){

    //프로필 초기 작성
    fun createProfile(requestMemberId: Long, profileUpdateDto: ProfileUpdateDto) {
        val member = memberRepository.findById(requestMemberId)
            .orElseThrow { MemberNotFoundException() }

        profileRepository.save(profileUpdateDto.toEntity(profileUpdateDto, member))
    }

    //프로필 수정
    fun updateProfile(requestMemberId: Long, profileUpdateDto: ProfileUpdateDto) {
        val profile = profileRepository.findByMemberId(requestMemberId)

        if (profile == null) {
            throw ProfileNullResponseException()
        }

        profile.update(
            birth = profileUpdateDto.birth,
            gender = profileUpdateDto.gender,
            location = profileUpdateDto.location,
            selfDescription = profileUpdateDto.selfDescription,
            mbti = profileUpdateDto.mbti,
            interestTag = profileUpdateDto.interestTag?.toMutableSet(),
            imageUrl = profileUpdateDto.imageUrl,
        )

    }

}