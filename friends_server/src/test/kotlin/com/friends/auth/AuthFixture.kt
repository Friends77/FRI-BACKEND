package com.friends.auth

import com.friends.member.createTestMember
import com.friends.member.entity.Authority
import com.friends.member.entity.Role
import org.springframework.security.core.GrantedAuthority

fun createTestAuthorities() =
    listOf(
        Authority(1L, Role.ROLE_USER, createTestMember()),
        Authority(2L, Role.ROLE_ADMIN, createTestMember()),
    ).map {
        GrantedAuthority({ it.role.name })
    }
