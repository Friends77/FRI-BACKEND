package com.friends.profile.service

import com.friends.member.MEMBER_ID
import com.friends.member.repository.MemberRepository
import com.friends.profile.createTestMember
import com.friends.profile.createTestProfile
import com.friends.profile.createTestProfileCreateDto
import com.friends.profile.repository.ProfileRepository
import com.friends.profile.updateTestProfile
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
                every { profileRepository.save(any()) } returns createTestProfile()

                `when`("유효한 프로필 정보를 전달하면") {
                    profileCommandService.createProfile(MEMBER_ID, createTestProfileCreateDto())

                    then("프로필이 저장되어야 한다.") {
                        verify(exactly = 1) { memberRepository.findById(MEMBER_ID) }
                        verify(exactly = 1) { profileRepository.save(match {
                                    it.birth == createTestProfileCreateDto().birth &&
                                    it.gender == createTestProfileCreateDto().gender &&
                                    it.location == createTestProfileCreateDto().location &&
                                    it.selfDescription == createTestProfileCreateDto().selfDescription &&
                                    it.mbti == createTestProfileCreateDto().mbti &&
                                    it.imageUrl == createTestProfileCreateDto().imageUrl
                        })}
                    }
                }
            }

            given("updateProfile 메서드를 호출할 때") {
                val existingProfile = createTestProfile()
                val updatedProfile = updateTestProfile()

                every { profileRepository.findByMemberId(MEMBER_ID) } returns existingProfile

                `when`("존재하는 프로필을 수정하면") {
                    profileCommandService.updateProfile(MEMBER_ID, updatedProfile)

                    then("수정된 프로필이 저장되어야 한다.") {
                        verify(exactly = 1) { profileRepository.findByMemberId(MEMBER_ID) }
                        existingProfile.birth shouldBe updatedProfile.birth
                        existingProfile.gender shouldBe updatedProfile.gender
                        existingProfile.location shouldBe updatedProfile.location
                        existingProfile.selfDescription shouldBe updatedProfile.selfDescription
                        existingProfile.mbti shouldBe updatedProfile.mbti
                        existingProfile.imageUrl shouldBe updatedProfile.imageUrl
                    }
                }
            }
        })