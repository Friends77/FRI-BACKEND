package com.friends.security.jwt

import com.friends.auth.TEST_ACCESS_TOKEN
import com.friends.auth.TEST_BEARER_ACCESS_TOKEN
import com.friends.auth.createTestCustomUserDetails
import com.friends.member.TEST_MEMBER_ID
import com.friends.security.userDetails.CustomUserDetailsService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import javax.crypto.SecretKey
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest : BehaviorSpec({
    val jwtTokenProvider = mockk<JwtTokenProvider>()
    val customUserDetailsService = mockk<CustomUserDetailsService>()
    val jwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService)

    val request = mockk<HttpServletRequest>()
    val response = mockk<HttpServletResponse>()
    val filterChain =
        mockk<FilterChain>(relaxed = true) // relaxed 모드란 모든 메서드에 대해 Mock 객체를 만들지만, 호출되지 않은 메서드에 대해 예외를 발생시키지 않는다.

    beforeEach {
        every { request.getAttribute(any()) } returns null
        every { request.dispatcherType } returns null
        every { request.setAttribute(any(), any()) } returns Unit
        every { request.removeAttribute(any()) } returns Unit
    }

    afterEach {
        SecurityContextHolder.clearContext()
        clearAllMocks()
    }

    given("JwtFilter 테스트") {
        `when`("유효한 토큰이 제공되면") {
            every { request.getHeader(AUTHORIZATION) } returns TEST_BEARER_ACCESS_TOKEN
            every { jwtTokenProvider.getMemberId(any<String>()) } returns TEST_MEMBER_ID
            every { jwtTokenProvider.key } returns mockk<SecretKey>()
            every { jwtTokenProvider.validateToken(any<String>()) } returns true
            val userDetails = createTestCustomUserDetails()
            every { customUserDetailsService.loadUserByUsername(any<String>()) } returns userDetails
            then("SecurityContextHolder 에 인증 정보가 설정되어야 한다") {
                jwtAuthenticationFilter.doFilter(request, response, filterChain)
                val authentication = SecurityContextHolder.getContext().authentication
                authentication.shouldBe(
                    UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities,
                    ),
                )
            }
        }

        `when`("유효하지 않은 토큰이 제공되면") {
            every { request.getHeader(AUTHORIZATION) } returns TEST_BEARER_ACCESS_TOKEN
            every { jwtTokenProvider.key } returns mockk<SecretKey>()
            every { jwtTokenProvider.validateToken(any<String>()) } returns false
            then("SecurityContextHolder 에 인증 정보가 설정되지 않아야 한다") {
                jwtAuthenticationFilter.doFilter(request, response, filterChain)
                val authentication = SecurityContextHolder.getContext().authentication
                authentication.shouldBeNull()
            }
        }

        `when`("Bearer 토큰이 아닌 토큰이 제공되면") {
            every { request.getHeader(AUTHORIZATION) } returns TEST_ACCESS_TOKEN
            then("SecurityContextHolder 에 인증 정보가 설정되지 않아야 한다") {
                jwtAuthenticationFilter.doFilter(request, response, filterChain)
                val authentication = SecurityContextHolder.getContext().authentication
                authentication.shouldBeNull()
            }
        }

        `when`("토큰이 제공되지 않으면") {
            every { request.getHeader(AUTHORIZATION) } returns null
            then("SecurityContextHolder 에 인증 정보가 설정되지 않아야 한다") {
                jwtAuthenticationFilter.doFilter(request, response, filterChain)
                val authentication = SecurityContextHolder.getContext().authentication
                authentication.shouldBeNull()
            }
        }

        `when`("토큰이 빈 문자열이면") {
            every { request.getHeader(AUTHORIZATION) } returns ""
            then("SecurityContextHolder 에 인증 정보가 설정되지 않아야 한다") {
                jwtAuthenticationFilter.doFilter(request, response, filterChain)
                val authentication = SecurityContextHolder.getContext().authentication
                authentication.shouldBeNull()
            }
        }

        `when`("토큰이 공백 문자열로 제공되면") {
            every { request.getHeader(AUTHORIZATION) } returns " "
            then("SecurityContextHolder 에 인증 정보가 설정되지 않아야 한다") {
                jwtAuthenticationFilter.doFilter(request, response, filterChain)
                val authentication = SecurityContextHolder.getContext().authentication
                authentication.shouldBeNull()
            }
        }
    }
})
