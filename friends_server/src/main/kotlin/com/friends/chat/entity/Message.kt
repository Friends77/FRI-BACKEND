package com.friends.chat.entity

import com.friends.common.entity.BaseMongoTimeEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "message")
class Message(
    val chatRoomId: String,
    val senderId: Long,
    val content: String,
    val type: MessageType,
    override var createdAt: LocalDateTime? = null,
) : BaseMongoTimeEntity() {
    /*
    id에 auto increment를 하기 위해서는 초기값을 String, nullable하게 설정해야하고
    val 대신 var로 선언해야 정상 작동한다. id 변경지점을 없애기 위해 private로 선언한다.
     */
    @Id
    var messageId: String? = null
        private set
}
