package com.friends.profile.service

import com.friends.member.repository.MemberRepository
import com.friends.profile.MEMBER_ID
import com.friends.profile.createTestMember
import com.friends.profile.createTestProfile
import com.friends.profile.repository.ProfileRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import java.util.*

class ProfileQueryServiceTest :
        BehaviorSpec({
            val profileRepository = mockk<ProfileRepository>()
            val memberRepository = mockk<MemberRepository>()
            val profileQueryService = ProfileQueryService(profileRepository, memberRepository)

            isolationMode = IsolationMode.InstancePerLeaf

            given("getProfile 메서드를 호출할 때") {
                val testProfile = createTestProfile()

                every { memberRepository.findById(MEMBER_ID) } returns Optional.of(createTestMember())
                every { profileRepository.findByMemberId(MEMBER_ID) } returns testProfile

                `when`("해당 회원에 대한 프로필이 존재하는 경우") {
                }

            }
        })