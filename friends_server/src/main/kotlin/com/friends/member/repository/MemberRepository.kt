package com.friends.member.repository

import com.friends.member.entity.Member
import com.friends.member.exception.EmailNotFoundException
import com.friends.member.exception.MemberNotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun MemberRepository.getByMemberId(memberId: Long): Member {
    return findById(memberId).orElseThrow { throw MemberNotFoundException() }
}

fun MemberRepository.getByEmail(email: String): Member {
    return findByEmail(email)
        ?: throw EmailNotFoundException()
}

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String): Member?
}
