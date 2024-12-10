package com.friends.profile.repository

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class ProfileRepositoryTest(
    @Autowired
    private val profileRepository: ProfileRepository,
)
