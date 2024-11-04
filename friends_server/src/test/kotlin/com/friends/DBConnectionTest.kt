package com.friends

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class DBConnectionTest(
    @Autowired private val jdbcTemplate: JdbcTemplate,
    @Autowired private val redissonClient: RedissonClient
) {
    @Test
    fun `PostgreSQL 연결 테스트`() {
        assertDoesNotThrow {
            jdbcTemplate.execute("SELECT 1")
        }
    }

    @Test
    fun `Redis 연결 테스트`() {
        assertDoesNotThrow {
            redissonClient.getBucket<String>("test").set("connection test")
        }
    }
}