package com.friends.alarm.repository

import com.friends.alarm.entity.Alarm
import com.friends.common.util.getList
import com.friends.member.entity.Member
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sorts.desc
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface AlarmRepository : JpaRepository<Alarm, Long>

interface AlarmRepositoryCustom {
    fun findAllByMemberIdBeforeId(
        memberId: Long,
        size: Int,
        lastAlarmId: Long,
    ): List<Alarm>
}

class AlarmRepositoryCustomImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : AlarmRepositoryCustom {
    override fun findAllByMemberIdBeforeId(
        memberId: Long,
        size: Int,
        lastAlarmId: Long,
    ): List<Alarm> =
        kotlinJdslJpqlExecutor.getList {
            select(entity(Alarm::class))
                .from(entity(Alarm::class))
                .where(
                    and(
                        path(Alarm::member).path(Member::id).equal(memberId),
                        path(Alarm::id).lessThan(lastAlarmId),
                    ),
                ).orderBy(desc(path(Alarm::id)))
        }
}
