package com.friends.member

import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

const val MEMBER_NAME = "test"
const val MEMBER_EMAIL = "test@gmail.com"
const val MEMBER_PASSWORD = "test1234"
val MEMBER_ID = 123L
val MEMBER_ID_WITHOUT_PROFILE = 123L

fun makeUserAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
    )

fun makeAdminAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
        SimpleGrantedAuthority("ROLE_ADMIN"),
    )

fun createTestMember(
    name: String = MEMBER_NAME,
    email: String = MEMBER_EMAIL,
    password: String = MEMBER_PASSWORD,
    oauth2Provider: OAuth2Provider? = null,
    imageUrl: String? = null,
) = Member.createUser(name, email, password, oauth2Provider, imageUrl)

fun createTestMemberWithoutProfile(): Member = Member(id = MEMBER_ID_WITHOUT_PROFILE, nickname = "test name2", email = "test@test2.com", password = "12345", oauth2Provider = OAuth2Provider.NAVER, imageUrl = "test imageurl2")
