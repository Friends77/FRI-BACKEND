package com.friends.common.exception

import com.friends.security.securityException.InvalidJwtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice // @ControllerAdvice와 @ResponseBody를 결합한 것
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(InvalidJwtException::class)
    fun handleInvalidJwtException(ex: InvalidJwtException): ResponseEntity<Any> {
        log.error("Invalid JWT", ex)
        return ResponseEntity.status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }
}
