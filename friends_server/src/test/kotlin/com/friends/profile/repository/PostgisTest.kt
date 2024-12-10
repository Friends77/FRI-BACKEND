package com.friends.profile.repository

import com.friends.profile.dto.LocationDto
import com.friends.support.annotation.RepositoryTest
import io.kotest.matchers.collections.shouldContainExactly
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@RepositoryTest
class PostgisTest(
    @Autowired private val testPointRepository: TestPointRepository,
    @Autowired private val entityManager: EntityManager,
) {
    @Test
    fun `5,000 미터 이내의 TestPoint를 검색할 때, 5,000 미터 이내의 TestPoint만 반환해야 합니다`() {
        // given
        val testPoint1 = TestPoint.of(LocationDto(longitude = 10.0, latitude = 10.0))
        val testPoint2 = TestPoint.of(LocationDto(longitude = 10.1, latitude = 10.1))
        val testPoint3 = TestPoint.of(LocationDto(longitude = 20.0, latitude = 20.0))

        val result1 = testPointRepository.save(testPoint1)
        testPointRepository.save(testPoint2)
        testPointRepository.save(testPoint3)

        entityManager.flush()
        entityManager.clear()

        // when
        val foundPoints = testPointRepository.findNodesWithinDistance(10.0, 10.0, 5000.0)

        // then
        foundPoints.map { it.id } shouldContainExactly listOf(result1.id)
    }

    @Test
    fun `20,000 미터 이내의 TestPoint를 검색할 때, 20,000 미터 이내의 TestPoint만 반환해야 합니다`() {
        // given
        val testPoint1 = TestPoint.of(LocationDto(longitude = 10.0, latitude = 10.0))
        val testPoint2 = TestPoint.of(LocationDto(longitude = 10.1, latitude = 10.1))
        val testPoint3 = TestPoint.of(LocationDto(longitude = 20.0, latitude = 20.0))

        val result1 = testPointRepository.save(testPoint1)
        val result2 = testPointRepository.save(testPoint2)
        testPointRepository.save(testPoint3)

        entityManager.flush()
        entityManager.clear()

        // when
        val foundPoints = testPointRepository.findNodesWithinDistance(10.0, 10.0, 20000.0)

        // then
        foundPoints.map { it.id } shouldContainExactly listOf(result1.id, result2.id)
    }

    @Test
    fun `20,000,000 미터 이내의 TestPoint를 검색할 때, 모든 TestPoint를 반환해야 합니다`() {
        // given
        val testPoint1 = TestPoint.of(LocationDto(longitude = 10.0, latitude = 10.0))
        val testPoint2 = TestPoint.of(LocationDto(longitude = 10.1, latitude = 10.1))
        val testPoint3 = TestPoint.of(LocationDto(longitude = 20.0, latitude = 20.0))

        val result1 = testPointRepository.save(testPoint1)
        val result2 = testPointRepository.save(testPoint2)
        val result3 = testPointRepository.save(testPoint3)

        entityManager.flush()
        entityManager.clear()

        // when
        val foundPoints = testPointRepository.findNodesWithinDistance(10.0, 10.0, 20000000.0)

        // then
        foundPoints.map { it.id } shouldContainExactly listOf(result1.id, result2.id, result3.id)
    }
}
