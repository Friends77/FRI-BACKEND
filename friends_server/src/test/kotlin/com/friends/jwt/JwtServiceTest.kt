package com.friends.jwt

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.GrantedAuthority

@SpringBootTest
class JwtServiceTest(
    @Autowired private val jwtService: JwtService,
) {
    @Test
    @DisplayName("createAccessToken 테스트")
    fun `createAccessToken 테스트`() {
        // given
        val memberId = 1L
        val authorities = listOf("ROLE_USER", "ROLE_ADMIN").map { GrantedAuthority { it } }

        // when
        val accessToken = jwtService.createAccessToken(memberId, authorities)

        // then
        memberId shouldBe jwtService.getMemberId(accessToken)
        authorities.map { it.authority } shouldContainExactly jwtService.getAuthorities(accessToken).map { it.authority }
    }

    @Test
    @DisplayName("validate 테스트")
    fun `validate 테스트`() {
        // given
        val memberId = 1L
        val authorities = listOf("ROLE_USER", "ROLE_ADMIN").map { GrantedAuthority { it } }
        val accessToken = jwtService.createAccessToken(memberId, authorities)

        // when
        val expectedTrue = jwtService.validate(accessToken)
        val expectedFalse = jwtService.validate(accessToken + "a")

        // then
        expectedTrue shouldBe true
        expectedFalse shouldBe false
    }
}
