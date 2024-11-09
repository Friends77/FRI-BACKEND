package com.friends.auth.controller

import com.friends.auth.dto.TokensResponse
import com.friends.common.exception.ErrorCode
import com.friends.common.swagger.ApiErrorCodeExamples
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Auth")
interface AuthControllerSpec {
    @Operation(
        summary = "로그인 API",
        responses = [
            ApiResponse(responseCode = "200", description = "로그인 성공"),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN,
        ],
    )
    fun logIn(
        @Parameter(hidden = true)
        socialAccessToken: String?,
    ): TokensResponse

    @Operation(
        summary = "토큰 갱신 API",
        description = "Refresh Token 을 통해 AccessToken 을 갱신합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "요청 성공"),
        ],
    )
    fun refresh(
        @Parameter(
            name = "token",
            description = "Bearer 같은 헤더는 붙이지 않고 전달해주세요. 예시 : eyJhbGciOiJIUzI1NiIsInR5cCI6....",
            required = true,
            `in` = ParameterIn.QUERY,
        )
        refreshToken: String?,
    ): TokensResponse
}
