package com.friends.member

import com.friends.member.entity.Member

const val TEST_MEMBER_ID = 1L
const val TEST_MEMBER_NAME = "test"
const val TEST_EMAIL = "email@naver.com"
const val TEST_PASSWORD = "password"
const val TEST_PROFILE_IMAGE = "profile-image"

fun createTestMember() =
    Member.createUser(TEST_MEMBER_NAME, TEST_EMAIL, TEST_PASSWORD, null, TEST_PROFILE_IMAGE)
