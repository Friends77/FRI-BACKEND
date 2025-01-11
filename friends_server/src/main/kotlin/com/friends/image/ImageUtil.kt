package com.friends.image

import org.apache.tika.Tika
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer

@Component
class ImageUtil {
    fun getContentType(byteArray: ByteArray): String {
        val inputStream = ByteArrayInputStream(byteArray)
        val tika = Tika()
        return tika.detect(inputStream)
    }
}
