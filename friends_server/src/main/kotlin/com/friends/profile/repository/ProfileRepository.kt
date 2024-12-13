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
        value = """
        SELECT 
            p.profile_id AS id,
            m.nickname AS nickname,
            p.image_url AS imageUrl,
            (6371000 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(p.location_latitude)) * 
            COS(RADIANS(p.location_longitude) - RADIANS(:longitude)) + 
            SIN(RADIANS(:latitude)) * SIN(RADIANS(p.location_latitude)))) AS distance
        FROM profile p
        INNER JOIN member m ON p.member_id = m.member_id
        WHERE (6371000 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(p.location_latitude)) * 
              COS(RADIANS(p.location_longitude) - RADIANS(:longitude)) + 
              SIN(RADIANS(:latitude)) * SIN(RADIANS(p.location_latitude)))) <= :distance
    """,
        nativeQuery = true,
    )
    fun findWithInDistance(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("distance") distance: Double,
    ): List<ProfileWithDistanceDto>
}
