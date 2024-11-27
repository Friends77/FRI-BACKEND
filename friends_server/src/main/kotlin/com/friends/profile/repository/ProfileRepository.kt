package com.friends.profile.repository

import com.friends.profile.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<Profile, Long> {

    fun findByMemberId(memberId: Long): Profile?
}