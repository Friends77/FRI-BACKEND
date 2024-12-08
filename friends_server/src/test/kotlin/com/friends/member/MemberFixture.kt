package com.friends.member

import com.friends.member.entity.Member
import com.friends.member.entity.OAuth2Provider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

val MEMBER_ID = 123L
val MEMBER_ID_WITHOUT_PROFILE = 2L

fun makeUserAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
    )

fun makeAdminAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
        SimpleGrantedAuthority("ROLE_ADMIN"),
    )

fun createTestMemberWithoutProfile(): Member = Member(id = MEMBER_ID_WITHOUT_PROFILE, name = "test name2", email = "test@test2.com", password= "12345", oauth2Provider = OAuth2Provider.NAVER, imageUrl = "test imageurl2" )
