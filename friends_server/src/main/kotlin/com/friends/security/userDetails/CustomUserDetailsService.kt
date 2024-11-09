package com.friends.security.userDetails

import com.friends.security.entity.Member
import com.friends.security.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository,
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val member: Member = memberRepository.findByEmail(email) ?: throw UsernameNotFoundException("존재하지 않는 이메일입니다.")
        return CustomUserDetails(member)
    }
}
