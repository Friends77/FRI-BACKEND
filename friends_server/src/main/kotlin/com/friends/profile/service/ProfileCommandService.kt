package com.friends.profile.service

import com.friends.member.repository.MemberRepository
import com.friends.profile.dto.ProfileDto
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
    fun createProfile(requestMemberId: Long, profileDto: ProfileDto) {
        val member = memberRepository.findById(requestMemberId)
            .orElseThrow{ IllegalArgumentException("멤버를 찾을 수 없습니다.") }

        profileRepository.save(profileDto.toEntity(profileDto, member))
    }

    //프로필 수정
    fun updateProfile(requestMemberId: Long, profileDto: ProfileDto) {
        val profile = profileRepository.findByMemberId(requestMemberId)
            ?: throw IllegalArgumentException("해당 멤버의 프로필이 존재하지 않습니다.")

        profile.update(
            birth = profileDto.birth,
            gender = profileDto.gender,
            location = profileDto.location,
            selfDescription = profileDto.selfDescription,
            mbti = profileDto.mbti,
            interestTag = profileDto.interestTag?.toMutableSet(),
            imageUrl = profileDto.imageUrl,
        )

    }

}