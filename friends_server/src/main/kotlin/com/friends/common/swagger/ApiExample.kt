package com.friends.common.swagger

import io.swagger.v3.oas.models.examples.Example

/*examples = Example().apply {
        summary = "로그인 성공"
        value = """
            {
                "status": "200",
                "data": {
                    "token": "123213"
                }
            }
        """
    },
    code = 200,
    name = "로그인 성공"*/
// TODO : 지우기
class ApiExample(
    val examples: Example, // 객체로 실제 예제 응답
    val code: Int, // 응답 코드
    val name: String, // 예제이름
)
