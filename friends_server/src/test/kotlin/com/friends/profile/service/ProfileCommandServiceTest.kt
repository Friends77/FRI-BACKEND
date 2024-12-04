package com.friends.profile.service

import com.friends.member.repository.MemberRepository
import com.friends.profile.repository.ProfileRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk

class ProfileCommandServiceTest :
        BehaviorSpec({
            val profileRepository = mockk<ProfileRepository>()
            val memberRepository = mockk<MemberRepository>()
            val profileCommandService = ProfileCommandService(profileRepository, memberRepository)
        })