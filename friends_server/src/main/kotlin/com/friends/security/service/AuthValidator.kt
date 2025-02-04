package com.friends.security.service

import com.friends.security.securityException.DuplicateNewPasswordException
import com.friends.security.securityException.InvalidPasswordException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthValidator(
    private val passwordEncoder: PasswordEncoder,
) {
    fun validateResetPassword(
        encryptedOldPassword: String,
        newPassword: String,
    ) {
        if (!isValidPasswordPattern(newPassword)) {
            throw InvalidPasswordException()
        }

        if (isPreviousPassword(encryptedOldPassword, newPassword)) {
            throw DuplicateNewPasswordException()
        }
    }

    private fun isValidPasswordPattern(password: String): Boolean {
        val lengthRegex = Regex(".{8,20}") // 길이 제한
        val lowerCaseRegex = Regex(".*[a-z].*") // 소문자 포함
        val digitRegex = Regex(".*[0-9].*") // 숫자 포함
        val specialCharRegex = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*") // 특수문자 포함
        val noWhiteSpaceRegex = Regex("^[^\\s]*\$") // 공백 금지

        return lengthRegex.matches(password) &&
            lowerCaseRegex.containsMatchIn(password) &&
            digitRegex.containsMatchIn(password) &&
            specialCharRegex.containsMatchIn(password) &&
            noWhiteSpaceRegex.matches(password)
    }

    private fun isPreviousPassword(
        passwordInDB: String,
        newPassword: String,
    ): Boolean = passwordEncoder.matches(newPassword, passwordInDB)
}
