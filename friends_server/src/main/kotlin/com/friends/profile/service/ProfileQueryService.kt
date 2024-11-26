package com.friends.profile.service

import com.friends.member.repository.MemberRepository
import com.friends.profile.dto.ProfileDto
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
    fun getProfile(requestMemberId: Long) : ProfileDto {
        val member = memberRepository.findById(requestMemberId)
            .orElseThrow { IllegalArgumentException("Member not found with id: $requestMemberId") }

        val nickname = member.name
        val email = member.email
        //회원가입 시 어떤 필드들이 채워지는지?
        val profileRes = ProfileDto(
            nickname = nickname,
            email = email,
        )
        return profileRes
    }

}