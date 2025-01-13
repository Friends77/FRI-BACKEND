package com.friends.config

import com.friends.category.entity.Category
import com.friends.category.entity.CategoryType
import com.friends.category.repository.CategoryRepository
import com.friends.member.entity.Member
import com.friends.member.repository.MemberRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class InitDB(
    private val categoryRepository: CategoryRepository,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
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
                    Member.createAdmin(
                        nickname = "admin",
                        email = "admin",
                        password = passwordEncoder.encode("admin"),
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
        }
}
