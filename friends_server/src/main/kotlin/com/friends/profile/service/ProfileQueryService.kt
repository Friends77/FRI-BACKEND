package com.friends.profile.service

import com.friends.member.repository.MemberRepository
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.repository.ProfileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileQueryService (
    val profileRepository: ProfileRepository,
    val memberRepository: MemberRepository,
) {

    //프로필 상세조회
    @Transactional(readOnly = true)
    fun getProfile(requestMemberId: Long) : ProfileResponseDto {
        val member = memberRepository.findById(requestMemberId)

        if(member.isEmpty){
            throw IllegalArgumentException("해당 멤버가 존재하지 않습니다.")
        }

        val profile = profileRepository.findByMemberId(requestMemberId)

        return profile.toResponseDto()
    }

}