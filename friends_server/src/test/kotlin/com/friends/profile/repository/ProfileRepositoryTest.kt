package com.friends.profile.repository

import com.friends.member.repository.MemberRepository
import com.friends.profile.createDBTestProfile
import com.friends.profile.createPoint
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class ProfileRepositoryTest(
    @Autowired
    private val profileRepository: ProfileRepository,
    @Autowired
    private val entityManager: EntityManager,
    @Autowired
    private val memberRepository: MemberRepository,
) : BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        given("다양한 위치를 가진 프로필이 저장되어 있는 경우") {
            val testProfile1 =
                createDBTestProfile().apply {
                    location = createPoint(10.0, 10.0)
                }
            val testProfile2 =
                createDBTestProfile().apply {
                    location = createPoint(10.1, 10.1)
                }
            val testProfile3 =
                createDBTestProfile().apply {
                    location = createPoint(20.0, 20.0)
                }

            memberRepository.save(testProfile1.member)
            val result1 = profileRepository.save(testProfile1)

            memberRepository.save(testProfile2.member)
            val result2 = profileRepository.save(testProfile2)

            memberRepository.save(testProfile3.member)
            val result3 = profileRepository.save(testProfile3)
            entityManager.flush()
            entityManager.clear()

            // (10,10) (10.1,10.1) 사이의 거리는 약 15,000 m 입니다
            // (10,10) (20,20) 사이의 거리는 10,000,000 m 이상입니다.
            `when`("20,000 미터 이내의 프로필을 검색할 때") {
                val foundProfiles = profileRepository.findNodesWithinDistance(10.0, 10.0, 20000.0)

                then("20,000 미터 이내의 프로필만 반환해야 합니다") {
                    foundProfiles.map { it.id } shouldContainExactly listOf(result1.id, result2.id)
                }
            }

            `when`("5,000 미터 이내의 프포필을 검색할 때") {
                val foundProfiles = profileRepository.findNodesWithinDistance(10.0, 10.0, 5000.0)

                then("5,000 미터 이내의 프로필이 없으므로 빈 목록을 반환해야 합니다") {
                    foundProfiles.map { it.id } shouldContainExactly listOf(testProfile1.id)
                }
            }

            `when`("20,000,000 미터 이내의 프로필을 검색할 때") {
                val foundProfiles = profileRepository.findNodesWithinDistance(0.0, 0.0, 20000000.0)

                then("20,000,000 미터 이내의 모든 프로필을 반환해야 합니다") {
                    foundProfiles.map { it.id } shouldContainExactly listOf(result1.id, result2.id, result3.id)
                }
            }
        }
    })
