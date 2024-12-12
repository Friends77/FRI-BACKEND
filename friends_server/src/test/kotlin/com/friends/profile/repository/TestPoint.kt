package com.friends.profile.repository

import com.friends.profile.dto.LocationDto
import com.friends.profile.entity.SpatialReferenceSystem
import io.lettuce.core.dynamic.annotation.Param
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Entity
class TestPoint(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Column(columnDefinition = "geometry(Point, ${SpatialReferenceSystem.WGS84})")
    var location: Point? = null,
) {
    companion object {
        fun of(locationDto: LocationDto): TestPoint = TestPoint(location = GeometryFactory().createPoint(Coordinate(locationDto.longitude, locationDto.latitude)))
    }
}

@Repository
interface TestPointRepository : JpaRepository<TestPoint, Long> {
    @Query(
        value = """
            SELECT *
            FROM test_point
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
    ): List<TestPoint>
}
