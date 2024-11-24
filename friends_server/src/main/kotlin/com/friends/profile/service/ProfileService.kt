package com.friends.profile.service

import com.friends.profile.repository.ProfileRepository
import org.springframework.stereotype.Service

@Service
class ProfileService (
    val profileRepository: ProfileRepository
) {

    fun getProfile(id: Long) {

    }

}