package com.friends.profile.repository

import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.profile.dto.LocationDto
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Profile
import com.friends.support.annotation.RepositoryTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@RepositoryTest
class ProfileRepositoryTest
    @Autowired
    constructor(
        private val profileRepository: ProfileRepository,
        private val memberRepository: MemberRepository,
    ) {
        private lateinit var profile1: Profile
        private lateinit var profile2: Profile
        private lateinit var profile3: Profile

        @BeforeEach
        fun setup() {
            val testPoint1 = LocationDto(10.0, 10.0)
            val testPoint2 = LocationDto(10.1, 10.1)
            val testPoint3 = LocationDto(20.0, 20.0)

            val member1 = memberRepository.save(Member.createUser("test1", "test1@com"))
            val member2 = memberRepository.save(Member.createUser("test2", "test2@com"))
            val member3 = memberRepository.save(Member.createUser("test3", "test3@com"))

            profile1 = profileRepository.save(Profile.of(member1, birth = LocalDate.now(), gender = GenderEnum.MAN, location = testPoint1, interestTag = mutableSetOf("일상"), imageUrl = "test imageurl"))
            profile2 = profileRepository.save(Profile.of(member2, birth = LocalDate.now(), gender = GenderEnum.MAN, location = testPoint2, interestTag = mutableSetOf("일상"), imageUrl = "test imageurl"))
            profile3 = profileRepository.save(Profile.of(member3, birth = LocalDate.now(), gender = GenderEnum.MAN, location = testPoint3, interestTag = mutableSetOf("일상"), imageUrl = "test imageurl"))
        }

        @Test
        fun `5000 미터 이내의 TestPoint 검색`() {
            // (10,10) (10.1,10.1) 사이의 거리는 약 15,000 m 입니다
            val foundPoints = profileRepository.findNodesWithinDistance(10.0, 10.0, 5000.0)
            assertEquals(listOf(profile1.id), foundPoints.map { it.id })
        }

        @Test
        fun `20000 미터 이내의 TestPoint 검색`() {
            // (10,10) (20,20) 사이의 거리는 10,000,000 m 이상입니다
            val foundPoints = profileRepository.findNodesWithinDistance(10.0, 10.0, 20000.0)
            assertEquals(listOf(profile1.id, profile2.id), foundPoints.map { it.id })
        }

        @Test
        fun `20000000 미터 이내의 TestPoint 검색`() {
            val foundPoints = profileRepository.findNodesWithinDistance(10.0, 10.0, 20000000.0)
            assertEquals(listOf(profile1.id, profile2.id, profile3.id), foundPoints.map { it.id })
        }
    }
