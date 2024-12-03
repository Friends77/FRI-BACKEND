package com.friends.profile

import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import com.friends.profile.entity.GenderEnum
import com.friends.profile.entity.MbtiEnum
import com.friends.profile.entity.Profile
import java.time.Instant
import java.util.Date

val MEMBER_ID = 1L
val PROFILE_ID = 1L

fun createTestMember(): Member = Member(id = MEMBER_ID, name = "test name", email = "test@test.com", password = "1234", oauth2Provider = OAuth2Provider.GOOGLE, imageUrl = "test imageurl")

fun createTestProfile(): Profile = Profile(id = PROFILE_ID, member = createTestMember(), birth = Date.from(Instant.now()), gender = GenderEnum.MAN, mbti = MbtiEnum.ENFJ, location = "test location", selfDescription = "test self description", imageUrl = "test imageurl" )