package com.friends

import com.mongodb.client.MongoClient
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class DBConnectionTest(
    private val jdbcTemplate: JdbcTemplate,
    private val redissonClient: RedissonClient,
    private val mongoClients: MongoClient,
) : BehaviorSpec(
    {
        given("PostgreSQL 연결 정보가 주어지면") {
            `when`("PostgreSQL 연결 테스트를 수행하면") {
                then("Query가 정상적으로 수행되어야 한다") {
                    shouldNotThrowAny {
                        jdbcTemplate.execute("SELECT 1")
                    }
                }
            }
        }

        given("Redis 연결 정보가 주어지면") {
            `when`("Redis 연결 테스트를 수행하면") {
                then("Bucket에 데이터가 정상적으로 저장되어야 한다") {
                    shouldNotThrowAny {
                        redissonClient.getBucket<String>("test").set("connection test")
                    }
                }
            }
        }

        given("MongoDB 연결 정보가 주어지면") {
            `when`("MongoDB 연결 테스트를 수행하면") {
                then("Database 목록을 조회할 수 있어야 한다") {
                    shouldNotThrowAny {
                        mongoClients.listDatabaseNames()
                        }
                    }
                }
            }
    },
)
