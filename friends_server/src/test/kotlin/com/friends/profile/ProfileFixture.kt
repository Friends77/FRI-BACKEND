package com.friends.profile

import com.friends.category.entity.Category
import com.friends.createTestCategory
import com.friends.member.MEMBER_ID
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.Location
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import com.friends.profile.entity.ProfileInterestTag
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

val PROFILE_ID = 1L
val PROFILE_IMAGE: MultipartFile =
    MockMultipartFile(
        "file", // 필드 이름
        "profile.img", // 파일 이름
        "image/jpeg", // 콘텐츠 타입
        byteArrayOf(1, 2, 3, 4), // 파일 내용 (예제 데이터)
    )

fun createTestMember(): Member = Member(id = MEMBER_ID, nickname = "test name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE)

fun createTestProfile(): Profile = Profile(id = PROFILE_ID, member = createTestMember(), birth = LocalDate.now(), gender = GenderEnum.MAN, mbti = MbtiEnum.ENFJ, location = Location(10.0, 10.0), selfDescription = "test self description", imageUrl = "test imageurl")

fun createTestProfileResponseDto(): ProfileResponseDto = ProfileResponseDto(nickname = createTestMember().nickname, email = "test@test.com", birth = LocalDate.now(), gender = GenderEnum.MAN, location = Location(10.0, 10.0), selfDescription = "test self description", imageUrl = "test imageurl", mbti = MbtiEnum.ENFJ)

fun createTestProfileCreateDto(): ProfileCreateDto = ProfileCreateDto(birth = LocalDate.now(), gender = GenderEnum.MAN, location = Location(10.0, 10.0), selfDescription = "test self description", imageUrl = "test imageurl")

fun updateTestProfile(): ProfileUpdateDto = ProfileUpdateDto(birth = LocalDate.now(), gender = GenderEnum.WOMAN, location = Location(20.0, 20.0), selfDescription = "test update self description", mbti = MbtiEnum.ENTJ, imageUrl = "test update imageurl")

fun createTestProfileInterestTag(
    profile: Profile = createTestProfile(),
    category: Category = createTestCategory(),
) = ProfileInterestTag(profile = profile, category = category)
