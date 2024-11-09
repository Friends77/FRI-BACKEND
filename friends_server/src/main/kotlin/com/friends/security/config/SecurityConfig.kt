package com.friends.security.config

import com.friends.security.userDetails.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*NOT_PERMITTED_URLS)
                    .authenticated()
                    .requestMatchers(*PERMITTED_URLS)
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.build() // TODO: exceptionHandling 추가해야함
}

val PERMITTED_URLS =
    arrayOf(
        "/swagger-ui/**",
        "/api/v1/auth/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
    )

val NOT_PERMITTED_URLS =
    arrayOf(
        "/api/v1/auth/log-out",
        // TODO:탈퇴도 막기
    )
