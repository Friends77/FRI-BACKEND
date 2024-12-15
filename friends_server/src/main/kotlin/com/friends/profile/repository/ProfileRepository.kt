package com.friends.profile.repository

import com.friends.profile.dto.ProfileWithDistanceDto
import com.friends.profile.entity.Profile
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProfileRepository : JpaRepository<Profile, Long> {
    fun findByMemberId(memberId: Long): Profile?

    // Haversine 공식을 사용해서 주어진 좌표에서 주어진 거리 이내에 있는 프로필을 찾는다.
    @Query(
        """
        SELECT new com.friends.profile.dto.ProfileWithDistanceDto(
            p.id, m.nickname, p.imageUrl,
            (6371000 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(p.location.latitude)) * 
            COS(RADIANS(p.location.longitude) - RADIANS(:longitude)) + 
            SIN(RADIANS(:latitude)) * SIN(RADIANS(p.location.latitude))))
        )
        FROM Profile p
        INNER JOIN Member m ON p.member.id = m.id
        WHERE (6371000 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(p.location.latitude)) * 
              COS(RADIANS(p.location.longitude) - RADIANS(:longitude)) + 
              SIN(RADIANS(:latitude)) * SIN(RADIANS(p.location.latitude)))) <= :distance
    """,
    )
    fun findWithInDistance(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("distance") distance: Double,
    ): List<ProfileWithDistanceDto>
}
