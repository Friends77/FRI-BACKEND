package com.friends.common.exception

import com.friends.board.BoardException
import com.friends.board.exception.CommentException
import com.friends.chat.ChatException
import com.friends.email.EmailException
import com.friends.member.MemberExceptions
import com.friends.oauth2.OAuth2Exception
import com.friends.profile.ProfileExceptions
import com.friends.security.securityException.AuthenticationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice // @ControllerAdvice와 @ResponseBody를 결합한 것
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AuthenticationException::class)
    fun handleInvalidJwtException(ex: AuthenticationException): ResponseEntity<Any> {
        log.error("Invalid JWT", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(EmailException::class)
    fun handleEmailException(ex: EmailException): ResponseEntity<Any> {
        log.error("Email Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(OAuth2Exception::class)
    fun handleOAuth2Exception(ex: OAuth2Exception): ResponseEntity<Any> {
        log.error("OAuth2 Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(BoardException::class)
    fun handleBoardException(ex: BoardException): ResponseEntity<Any> {
        log.error("Board Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(ProfileExceptions::class)
    fun handleProfileException(ex: ProfileExceptions): ResponseEntity<Any> {
        log.error("Profile Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(MemberExceptions::class)
    fun handleMemberException(ex: MemberExceptions): ResponseEntity<Any> {
        log.error("Member Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(ChatException::class)
    fun handleChatException(ex: ChatException): ResponseEntity<Any> {
        log.error("Chat Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

    @ExceptionHandler(CommentException::class)
    fun handleCommentException(ex: CommentException): ResponseEntity<Any> {
        log.error("Comment Exception", ex)
        return ResponseEntity
            .status(ex.errorCode.httpStatus)
            .body(ErrorResponse.of(ex.errorCode, ex.message))
    }

//    @ExceptionHandler(Exception::class)
//    fun handleException(ex: Exception): ResponseEntity<Any> {
//        log.error("Exception", ex)
//        return ResponseEntity
//            .status(ex.httpStatus)
//            .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.message))
//    }
}
