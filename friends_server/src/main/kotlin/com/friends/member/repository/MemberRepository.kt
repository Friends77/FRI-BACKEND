package com.friends.member.repository

import com.friends.member.NotFoundMemberException
import com.friends.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun MemberRepository.getByMemberId(memberId: Long): Member = findById(memberId).orElseThrow { throw NotFoundMemberException(memberId) }

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByEmail(email: String): Member?

    fun existsByEmail(email: String): Boolean
}
