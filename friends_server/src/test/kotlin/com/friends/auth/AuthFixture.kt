package com.friends.auth

import com.friends.member.createTestMember
import com.friends.member.entity.Authority
import com.friends.member.entity.Role
import com.friends.security.jwt.generateHmac256Key
import com.friends.security.userDetails.CustomUserDetails
import org.springframework.security.core.GrantedAuthority

val TEST_ACCESS_TOKEN = generateHmac256Key()

val TEST_BEARER_ACCESS_TOKEN = "Bearer $TEST_ACCESS_TOKEN"

fun createTestAuthorities() =
    listOf(
        Authority(1L, Role.ROLE_USER, createTestMember()),
        Authority(2L, Role.ROLE_ADMIN, createTestMember()),
    ).map {
        GrantedAuthority({ it.role.name })
    }

fun createTestSecurityUser() = CustomUserDetails(createTestMember())
