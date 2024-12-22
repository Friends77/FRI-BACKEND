package com.friends.message

import com.friends.chat.createTestChatRoom
import com.friends.chat.entity.ChatRoom
import com.friends.member.createTestMember
import com.friends.member.entity.Member
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import java.time.LocalDateTime

const val TEST_CONTENT = "안녕하세여"

fun createTestMessage(
    chatRoom: ChatRoom = createTestChatRoom(),
    sender: Member = createTestMember(),
    content: String = TEST_CONTENT,
    type: MessageType = MessageType.TEXT,
) = Message.of(content = content, chatRoom = chatRoom, sender = sender, type = type)

class MockTestMessage(
    id: Long = 0L,
    chatRoom: ChatRoom = createTestChatRoom(),
    sender: Member = createTestMember(),
    content: String = TEST_CONTENT,
    type: MessageType = MessageType.TEXT,
) : Message(id, chatRoom, sender, content, type) {
    override var createdAt: LocalDateTime = LocalDateTime.now() // 해당 mock 객체를 만들어서 채팅방 리스트의 마지막 메세지 시간을 테스트하기 위함
    // 코틀린은 기본적으로 final이기 때문에 open 키워드를 붙여주어야 상속이 가능하다.
}
