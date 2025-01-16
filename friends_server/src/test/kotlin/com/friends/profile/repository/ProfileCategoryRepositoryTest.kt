package com.friends.profile.repository

import com.friends.category.entity.Category
import com.friends.category.entity.CategoryType
import com.friends.category.repository.CategoryRepository
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.profile.createTestProfile
import com.friends.profile.createTestProfileInterestTag
import com.friends.profile.entity.Profile
import com.friends.support.annotation.RepositoryTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Pageable

@RepositoryTest
class ProfileCategoryRepositoryTest(
    private val profileRepository: ProfileRepository,
    private val memberRepository: MemberRepository,
    private val profileInterestTagRepository: ProfileInterestTagRepository,
    private val categoryRepository: CategoryRepository,
    private val entityManager: EntityManager,
) : DescribeSpec(
        {
            lateinit var member1: Member
            lateinit var member2: Member
            lateinit var profile1: Profile
            lateinit var profile2: Profile
            lateinit var category1: Category
            lateinit var category2: Category

            beforeEach {
                member1 = memberRepository.save(createTestMember())
                member2 = memberRepository.save(createTestMember(email = "test2"))

                profile1 = profileRepository.save(createTestProfile(member = member1))
                profile2 = profileRepository.save(createTestProfile(member = member2))

                category1 = categoryRepository.save(Category(name = "카테고리1", type = CategoryType.SUBJECT, image = "🎵"))
                category2 = categoryRepository.save(Category(name = "카테고리2", type = CategoryType.SUBJECT, image = "🎈"))

                profileInterestTagRepository.save(createTestProfileInterestTag(profile1, category = category1))
                profileInterestTagRepository.save(createTestProfileInterestTag(profile1, category = category2))

                profileInterestTagRepository.save(createTestProfileInterestTag(profile2, category = category1))

                entityManager.flush()
                entityManager.clear()
            }

            describe("findProfileWithCategoryIds 메서드는") {
                context("카테고리 id 목록이 주어졌을 때") {
                    it("카테고리가 하나라도 누락되면 반환하지 않는다.") {

                        val profileSlice =
                            profileRepository.findProfileWithCategoryIds(
                                categoryIds = listOf(category1.id, category2.id),
                                pageable = Pageable.ofSize(3),
                            )

                        println(profileRepository.findAll())
                        println(profileInterestTagRepository.findAll())

                        profileSlice.content.map { it.id } shouldContainExactly listOf(profile1.id)
                    }

                    it("해당 카테고리를 포함한 유저는 모두 반환한다.") {
                        val profileSlice =
                            profileRepository.findProfileWithCategoryIds(
                                categoryIds = listOf(category1.id),
                                pageable = Pageable.ofSize(3),
                            )

                        profileSlice.content.map { it.id } shouldContainExactly listOf(profile1.id, profile2.id)
                    }
                }
            }
        },
    )
