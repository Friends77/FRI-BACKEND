package com.friends.security.jwt

import java.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private val hMacKeyGenerator = KeyGenerator.getInstance("HmacSHA256") // 해당 알고리즘으로 키 생성기 만듦

fun generateHmac256Key(): String {
    val secretKey: SecretKey = hMacKeyGenerator.generateKey()
    val secretKeyBytes: ByteArray = secretKey.encoded
    return Base64.getEncoder().encodeToString(secretKeyBytes)
}
