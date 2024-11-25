package com.friends.member

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

val MEMBER_ID = 123L

fun makeUserAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
    )

fun makeAdminAuthorities(): Collection<GrantedAuthority> =
    listOf(
        SimpleGrantedAuthority("ROLE_USER"),
        SimpleGrantedAuthority("ROLE_ADMIN"),
    )
