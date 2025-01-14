package com.friends.alarm.repository

import com.friends.alarm.entity.Alarm
import com.friends.common.util.getSlice
import com.friends.member.entity.Member
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sorts.desc
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface AlarmRepository :
    JpaRepository<Alarm, Long>,
    AlarmCustomRepository

interface AlarmCustomRepository {
    fun findAllByMemberIdBeforeId(
        memberId: Long,
        size: Int,
        lastAlarmId: Long? = null,
    ): Slice<Alarm>
}

class AlarmCustomRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : AlarmCustomRepository {
    override fun findAllByMemberIdBeforeId(
        memberId: Long,
        size: Int,
        lastAlarmId: Long?,
    ): Slice<Alarm> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(Alarm::class))
                .from(entity(Alarm::class))
                .where(
                    and(
                        path(Alarm::sender).path(Member::id).equal(memberId),
                        lastAlarmId?.let { path(Alarm::id).lessThan(it) },
                    ),
                ).orderBy(desc(path(Alarm::id)))
        }
    }
}
