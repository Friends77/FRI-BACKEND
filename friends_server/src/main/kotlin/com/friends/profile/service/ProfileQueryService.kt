package com.friends.profile.service

import com.friends.member.repository.MemberRepository
import com.friends.profile.entity.Profile
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
    fun getProfile(requestMemberId: Long) : Profile {
        val member = memberRepository.findById(requestMemberId)

        if(member.isEmpty){
            throw IllegalArgumentException()
        }

        val profile = profileRepository.findByMemberId(requestMemberId)

        return profile
    }

}