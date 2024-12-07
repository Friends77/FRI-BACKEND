package com.friends.profile.service

import com.friends.member.repository.MemberRepository
import com.friends.profile.MEMBER_ID
import com.friends.profile.createTestMember
import com.friends.profile.createTestProfile
import com.friends.profile.createTestProfileCreateDto
import com.friends.profile.entity.Profile
import com.friends.profile.repository.ProfileRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class ProfileCommandServiceTest :
        BehaviorSpec({
            val profileRepository = mockk<ProfileRepository>()
            val memberRepository = mockk<MemberRepository>()
            val profileCommandService = ProfileCommandService(profileRepository, memberRepository)

            isolationMode = IsolationMode.InstancePerLeaf

            given("createProfile 메서드를 호출할 때") {
                every { memberRepository.findById(MEMBER_ID) } returns Optional.of(createTestMember())
                every { profileRepository.save(any()) } answers {firstArg<Profile>() }

                val profile = createTestProfile()

                `when`("유효한 프로필 정보를 전달하면") {
                    val savedProfile = profileCommandService.createProfile(MEMBER_ID, createTestProfileCreateDto())

                    then("프로필이 저장되어야 한다.") {
                        savedProfile shouldBe profile
                        verify(exactly = 1) { profileRepository.save(profile)}
                    }
                }
            }

            given("updateProfile 메서드를 호출할 때") {
                val existingProfile = createTestProfile()
//                val updatedProfile = createTestProfile().

                every { profileRepository.findById(existingProfile.id!!) } returns Optional.of(existingProfile)
                every { profileRepository.findByMemberId(MEMBER_ID) } returns createTestProfile()
                every { profileRepository.save(any()) } answers {firstArg<Profile>() }


            }

        })