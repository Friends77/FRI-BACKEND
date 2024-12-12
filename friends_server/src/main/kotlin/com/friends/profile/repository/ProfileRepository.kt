package com.friends.profile.repository

import com.friends.profile.entity.Profile
import com.friends.profile.entity.SpatialReferenceSystem
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProfileRepository : JpaRepository<Profile, Long> {
    fun findByMemberId(memberId: Long): Profile?

    // distance 미터 이내의 프로필을 조회합니다.
    @Query(
        value = """
            SELECT *
            FROM Profile
            WHERE ST_DWithin(
                ST_Transform(location, ${SpatialReferenceSystem.WEBMERCATOR}), 
                ST_Transform(ST_SetSRID(ST_MakePoint(:lng, :lat), ${SpatialReferenceSystem.WGS84}), ${SpatialReferenceSystem.WEBMERCATOR}),
                :distance
            )
        """,
        nativeQuery = true,
    )
    fun findNodesWithinDistance(
        @Param("lng") lng: Double,
        @Param("lat") lat: Double,
        @Param("distance") distance: Double,
    ): List<Profile>
}
