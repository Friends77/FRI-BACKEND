package com.friends.profile

import com.friends.member.MEMBER_ID
import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.dto.ProfileUpdateDto
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import java.time.LocalDate

val PROFILE_ID = 1L

fun createTestMember(): Member = Member(id = MEMBER_ID, nickname = "test name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE, imageUrl = "test imageurl")

fun createTestProfile(): Profile = Profile(id = PROFILE_ID, member = createTestMember(), birth = LocalDate.now(), gender = GenderEnum.MAN, mbti = MbtiEnum.ENFJ, location = "test location", selfDescription = "test self description", imageUrl = "test imageurl")

fun createTestProfileResponseDto(): ProfileResponseDto = ProfileResponseDto(nickname = createTestMember().nickname, email = "test@test.com", birth = LocalDate.now(), gender = GenderEnum.MAN, location = "test location", selfDescription = "test self description", imageUrl = "test imageurl", mbti = MbtiEnum.ENFJ)

fun createTestProfileCreateDto(): ProfileCreateDto = ProfileCreateDto(birth = LocalDate.now(), gender = GenderEnum.MAN, location = "test location", selfDescription = "test self description", imageUrl = "test imageurl")

fun updateTestProfile(): ProfileUpdateDto = ProfileUpdateDto(birth = LocalDate.now(), gender = GenderEnum.WOMAN, location = "test update location", selfDescription = "test update self description", mbti = MbtiEnum.ENTJ, imageUrl = "test update imageurl")
