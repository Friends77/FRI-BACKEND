package com.friends.security.jwt

import com.friends.auth.createTestAuthorities
import com.friends.member.TEST_MEMBER_ID
import com.friends.member.entity.Role
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.mockk.unmockkAll
import org.springframework.test.util.ReflectionTestUtils

class JwtTokenProviderTest :
    BehaviorSpec({
        val secretKey = generateHmac256Key()
        val jwtTokenProvider = JwtTokenProvider()

        beforeTest {
            ReflectionTestUtils.setField(
                jwtTokenProvider,
                "secretKey",
                secretKey,
            ) //  private 필드에 값을 주입
        }

        afterTest {
            unmockkAll() // 모든 Mock 객체를 해제
        }

        given("createAccessToken 메서드") {
            `when`("유효한 memberId, authorities 를 전달하면") {
                then("AccessToken 이 반환된다") {
                    val authorities = createTestAuthorities()
                    val accessToken =
                        jwtTokenProvider.createAccessToken(TEST_MEMBER_ID, authorities)
                    accessToken.shouldStartWith("ey")
                }
            }
        }

        given("createRefreshToken 메서드") {
            `when`("유효한 memberId, authorities, rotateId 를 전달하면") {
                then("RefreshToken 이 반환된다") {
                    val memberId = 2L
                    val authorities = createTestAuthorities()
                    val rotateId = jwtTokenProvider.createRotateId()
                    val refreshToken =
                        jwtTokenProvider.createRefreshToken(memberId, authorities, rotateId)
                    refreshToken.shouldStartWith("ey")
                }
            }
        }

        given("validateToken 메서드") {
            `when`("유효한 Token 을 전달하면") {
                then("true 가 반환된다") {
                    val accessToken =
                        jwtTokenProvider.createAccessToken(TEST_MEMBER_ID, createTestAuthorities())
                    jwtTokenProvider.validateToken(accessToken) shouldBe true
                }
            }

            `when`("만료된 Token을 전달하면") {
                then("false 가 반환된다") {
                    val accessToken =
                        jwtTokenProvider.createToken(
                            mapOf(
                                "memberId" to TEST_MEMBER_ID.toString(),
                                "authorities" to createTestAuthorities().map { it.authority },
                            ),
                            0,
                        )
                    jwtTokenProvider.validateToken(accessToken) shouldBe false
                }
            }

            `when`("유효하지 않은 Token 을 전달하면") {
                then("false 가 반환된다") {
                    jwtTokenProvider.validateToken("invalidToken") shouldBe false
                }
            }
        }

        given("getUserId 메서드") {
            `when`("유효한 AccessToken 을 전달하면") {
                then("UserId 가 반환된다") {
                    val accessToken =
                        jwtTokenProvider.createAccessToken(TEST_MEMBER_ID, createTestAuthorities())
                    jwtTokenProvider.getMemberId(accessToken) shouldBe TEST_MEMBER_ID
                }
            }
        }

        given("getAuthorities 메소드") {
            `when`("유효한 AccessToken 을 전달하면") {
                then("Authorities 가 반환된다") {
                    val accessToken =
                        jwtTokenProvider.createAccessToken(TEST_MEMBER_ID, createTestAuthorities())
                    val authorities = jwtTokenProvider.getAuthorities(accessToken)
                    authorities.map { it.authority } shouldBe
                            listOf(
                                Role.ROLE_USER.name,
                                Role.ROLE_ADMIN.name,
                            )
                }
            }
        }
    })
