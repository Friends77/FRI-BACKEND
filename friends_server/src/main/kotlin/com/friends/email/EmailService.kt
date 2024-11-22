package com.friends.email

import jakarta.mail.Message
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun sendHtml(
        to: String,
        subject: String,
        html: String,
    ) {
        try {
            val message = javaMailSender.createMimeMessage()
            message.subject = subject
            message.setText(html, "utf-8", "html")
            message.setRecipients(
                Message.RecipientType.TO,
                to,
            )
            javaMailSender.send(message)
        } catch (e: Exception) {
            log.error("Failed to send email", e)
            throw EmailSendFailedException()
        }
    }
}
