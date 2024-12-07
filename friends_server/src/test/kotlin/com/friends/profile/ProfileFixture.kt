package com.friends.profile

import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.profile.dto.ProfileCreateDto
import com.friends.profile.dto.ProfileResponseDto
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import java.time.LocalDate

val MEMBER_ID = 1L
val PROFILE_ID = 1L
val MEMBER_ID_WITHOUT_PROFILE = 2L

fun createTestMember(): Member = Member(id = MEMBER_ID, name = "test name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE, imageUrl = "test imageurl")

fun createTestProfile(): Profile = Profile(id = PROFILE_ID, member = createTestMember(), birth = LocalDate.now(), gender = GenderEnum.MAN, mbti = MbtiEnum.ENFJ, location = "test location", selfDescription = "test self description", imageUrl = "test imageurl" )

fun createTestProfileResponseDto(): ProfileResponseDto = ProfileResponseDto(nickname = createTestMember().name, email = "test@test.com", birth = LocalDate.now(), gender = GenderEnum.MAN, location = "test location", selfDescription = "test self description", imageUrl = "test imageurl" , mbti = MbtiEnum.ENFJ)

fun createTestMemberWithoutProfile(): Member = Member(id = MEMBER_ID_WITHOUT_PROFILE, name = "test name2", email = "test@test2.com", password= "12345", oauth2Provider = OAuth2Provider.NAVER, imageUrl = "test imageurl2" )

fun createTestProfileCreateDto(): ProfileCreateDto = ProfileCreateDto(birth = LocalDate.now(), gender = GenderEnum.MAN, location = "test location", selfDescription = "test self description", imageUrl = "test imageurl" )