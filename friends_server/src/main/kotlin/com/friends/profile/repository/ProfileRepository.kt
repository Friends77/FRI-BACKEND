package com.friends.profile.repository

import com.friends.profile.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProfileRepository : JpaRepository<Profile, Long> {
    fun findByMemberId(memberId: Long): Profile?

    @Query(
        value = """
            SELECT *
            FROM Profile
            WHERE ST_DWithin(
                ST_Transform(geometry_point, 3857), 
                ST_Transform(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326), 3857),
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
