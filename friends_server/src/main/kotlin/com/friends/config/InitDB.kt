package com.friends.config

import com.friends.category.entity.Category
import com.friends.category.entity.CategoryType
import com.friends.category.repository.CategoryRepository
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Location
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import com.friends.profile.repository.ProfileInterestTagRepository
import com.friends.profile.repository.ProfileRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import kotlin.random.Random

@Component
class InitDB(
    private val categoryRepository: CategoryRepository,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val profileRepository: ProfileRepository,
    private val profileInterestTagRepository: ProfileInterestTagRepository,
) {
    @Bean
    fun runInitializer(): ApplicationRunner =

        ApplicationRunner {
            // 테스트 유저 생성
            memberRepository.saveAll(
                listOf(
                    Member.createUser(
                        nickname = "user",
                        email = "user",
                        password = passwordEncoder.encode("user"),
                    ),
                    Member.createUser(
                        nickname = "user2",
                        email = "user2",
                        password = passwordEncoder.encode("user2"),
                    ),
                ),
            )

            // 카테고리 생성
            categoryRepository.deleteAll()
            categoryRepository.save(Category(name = "자유수다", type = CategoryType.SUBJECT, image = "🧑‍🧑‍🧒"))
            categoryRepository.save(Category(name = "팬덤", type = CategoryType.SUBJECT, image = "🎈"))
            categoryRepository.save(Category(name = "게임", type = CategoryType.SUBJECT, image = "🎮"))
            categoryRepository.save(Category(name = "음악", type = CategoryType.SUBJECT, image = "🎵"))
            categoryRepository.save(Category(name = "맛집", type = CategoryType.SUBJECT, image = "🍽️"))
            categoryRepository.save(Category(name = "고민/상담", type = CategoryType.SUBJECT, image = "🗣️"))
            categoryRepository.save(Category(name = "엔터테인먼트", type = CategoryType.SUBJECT, image = "🎈"))
            categoryRepository.save(Category(name = "스포츠", type = CategoryType.SUBJECT, image = "🏆"))
            categoryRepository.save(Category(name = "일상", type = CategoryType.SUBJECT, image = "☘️"))
            categoryRepository.save(Category(name = "운동/건강", type = CategoryType.SUBJECT, image = "👟️"))
            categoryRepository.save(Category(name = "여행", type = CategoryType.SUBJECT, image = "✈️"))
            categoryRepository.save(Category(name = "취미", type = CategoryType.SUBJECT, image = "🧸"))
            categoryRepository.save(Category(name = "친목/모임", type = CategoryType.SUBJECT, image = "👏"))
            categoryRepository.save(Category(name = "패션/뷰티", type = CategoryType.SUBJECT, image = "🧢"))
            categoryRepository.save(Category(name = "직업", type = CategoryType.SUBJECT, image = "💼"))
            categoryRepository.save(Category(name = "자기계발", type = CategoryType.SUBJECT, image = "📚"))
            categoryRepository.save(Category(name = "재테크", type = CategoryType.SUBJECT, image = "💰"))
            categoryRepository.save(Category(name = "서울", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "부산", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "대구", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "인천", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "광주", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "대전", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "울산", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "세종", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "경기도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "강원도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "충청북도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "충청남도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "전라북도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "전라남도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "경상북도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "경상남도", type = CategoryType.REGION))
            categoryRepository.save(Category(name = "제주도", type = CategoryType.REGION))

            // 100 명의 테스트 유저 생성
            val categories = categoryRepository.findAll()

            for (i in 1..100) {
                val member =
                    memberRepository.save(
                        Member.createUser(
                            nickname = "test$i",
                            email = "test$i",
                            password = passwordEncoder.encode("test$i"),
                        ),
                    )
                val randomBirth =
                    LocalDate.of(
                        (1990..1999).random(),
                        (1..12).random(),
                        (1..28).random(),
                    )
                val randomGender = GenderEnum.entries.toTypedArray().random()
                val randomLatitude = 38 + Random.nextDouble(0.0, 1.0)
                val randomLongitude = 127 + Random.nextDouble(0.0, 1.0)
                val randomLocation = Location(randomLatitude, randomLongitude)
                val randomMbti = MbtiEnum.entries.toTypedArray().random()

                val profile =
                    profileRepository.save(
                        Profile(
                            member = member,
                            // 생일은 1990년 1월 1일부터 1999년 12월 31일 사이의 랜덤한 날짜로 설정
                            birth = randomBirth,
                            gender = randomGender,
                            imageUrl = "https://friends-bucket.s3.ap-northeast-2.amazonaws.com/2d397027-6448-414a-b155-17b5a41f6c34",
                            location = randomLocation,
                            mbti = randomMbti,
                        ),
                    )

                // 1~10개의 랜덤한 카테고리를 저장
                val randomCategoryCount = (1..10).random()
                val randomCategories = categories.shuffled().take(randomCategoryCount)

                randomCategories.map {
                    profileInterestTagRepository.save(
                        ProfileInterestTag(
                            profile = profile,
                            category = it,
                        ),
                    )
                }
            }
        }
}
