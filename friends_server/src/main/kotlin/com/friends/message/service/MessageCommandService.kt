package com.friends.message.service

import com.friends.chat.ChatRoomNotFoundException
import com.friends.chat.dto.ChatSendMessageDto
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.common.util.JsonUtil
import com.friends.member.MemberNotFoundException
import com.friends.member.repository.MemberRepository
import com.friends.message.entity.Message
import com.friends.message.entity.MessageType
import com.friends.message.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService

@Service
class MessageCommandService(
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val executor: ExecutorService,
) {
    /**
     * 채팅방 ID를 키로 하고, 참여하고 있는 온라인 유저의 아이디를 값으로 하는 Map입니다.
     * ConcurrentHashMap을 사용하여 thread-safe하게 구현합니다.
     * value 의 MutableSet은 thread-safe 하지 않아서 ConcurrentHashMap.newKeySet()을 사용하여 thread-safe하게 구현합니다.
     */
    private val onlineUsers = ConcurrentHashMap<Long, MutableSet<Long>>()

    /**
     * 유저 ID를 키로 하고, 참여하고 있는 온라인 유저의 세션을 값으로 하는 Map입니다.
     * 하나의 유저가 여러개의 세션을 가질 수 있기 때문에 MutableSet을 사용합니다. (ex. 웹, 모바일 에서 동시 접속)
     */
    private val sessions = ConcurrentHashMap<Long, MutableSet<WebSocketSession>>()

    /**
     * 참여하고 있는 모든 채팅방에 온라인 유저로 등록됩니다.
     */
    fun setAllChatRoomsOnline(
        memberId: Long,
        session: WebSocketSession,
    ) {
        sessions
            .computeIfAbsent(memberId) {
                ConcurrentHashMap.newKeySet()
            }.add(session)

        chatRoomMemberRepository
            .findAllByMemberId(memberId)
            .forEach { chatRoomMember ->
                onlineUsers
                    .computeIfAbsent(chatRoomMember.chatRoom.id) {
                        ConcurrentHashMap.newKeySet()
                    }.add(memberId)
            }
    }

    /**
     * 참여하고 있는 모든 채팅방에서 오프라인 상태가 됩니다.
     */
    fun setAllChatRoomsOffline(
        memberId: Long,
        session: WebSocketSession,
    ) {
        sessions[memberId]?.remove(session)

        chatRoomMemberRepository
            .findAllByMemberId(memberId)
            .forEach { chatRoomMember ->
                onlineUsers[chatRoomMember.chatRoom.id]?.remove(memberId)
            }
    }

    /**
     * 유저가 채팅방에 온라인 상태가 됩니다.
     */
    fun setChatRoomOnline(
        memberId: Long,
        chatRoomId: Long,
    ) {
        // 웹소켓이 연결되지 않은 경우에는 무시합니다.
        if (sessions[memberId]?.isEmpty() == true) {
            return
        }
        onlineUsers
            .computeIfAbsent(chatRoomId) {
                ConcurrentHashMap.newKeySet()
            }.add(memberId)
    }

    /**
     * 유저가 채팅방에 오프라인 상태가 됩니다.
     */
    fun setChatRoomOffline(
        memberId: Long,
        chatRoomId: Long,
    ) {
        onlineUsers[chatRoomId]?.remove(memberId)
    }

    /**
     * 채팅방을 나갈 때마다 마지막으로 읽은 메세지 ID를 업데이트합니다.
     * 채팅방이 없거나 멤버가 없거나 채팅방 멤버가 아닐 경우 무시합니다.
     */
    @Transactional
    fun disconnectChatRoom(
        chatRoomId: Long,
        memberId: Long,
    ) {
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null) ?: return // 채팅방이 없을 경우 무시
        val member = memberRepository.findById(memberId).orElse(null) ?: return // 멤버가 없을 경우 무시
        val chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member) ?: return // 채팅방 멤버가 아닐 경우 무시

        // 마지막으로 읽은 메세지 ID 업데이트 (채팅방에 메세지가 없다면 무시)
        messageRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)?.let {
            chatRoomMember.lastReadMessage = it
        }
    }

    /**
     * 채팅방에 메세지를 보내고 메세지를 저장합니다.
     * 채팅방이 없거나 멤버가 없을 경우 예외를 발생시킵니다.
     */
    @Transactional
    fun sendMessage(
        chatRoomId: Long,
        memberId: Long,
        content: String,
        type: MessageType,
    ): Message {
        /**
         * 메세지를 보낼 때마다 보낸 유저와 채팅방이 있는지 DB 에 확인합니다.
         * 이 과정이 비효율적일 경우 아래 프록시 객체를 생성하여 메세지를 보내는 로직을 고려합니다.
         * 프록시 객체는 DB 에서 채팅방과 유저 정보를 요청하지 않지만, DB 에 데이터가 있는지 없는지 확인할 수 없습니다.
         *
         * val chatRoom = entityManager.getReference(ChatRoom::class.java, chatRoomId)
         * val sender = entityManager.getReference(Member::class.java, message.senderId)
         */
        val chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow { ChatRoomNotFoundException() }
        val sender = memberRepository.findById(memberId).orElseThrow { MemberNotFoundException() }
        val message = messageRepository.save(Message.of(chatRoom, sender, content, type))

        // 채팅방의 모든 온라인 유저에게 메세지 전송
        val onlineUserIdSet = onlineUsers[chatRoomId] ?: return message// 온라인 유저가 없으면 메세지를 보낼 필요가 없습니다.
        onlineUserIdSet.forEach {
            val sessions = sessions[it] ?: return@forEach // 온라인 유저의 세션이 없으면 메세지를 보낼 필요가 없습니다.
            // 온라인 유저와 연결된 모든 웹소켓에 메세지 전송
            sessions.forEach { session ->
                CompletableFuture // 비동기 처리
                    .supplyAsync(
                        {
                            if (session.isOpen) {
                                try {
                                    session.sendMessage(
                                        TextMessage(
                                            JsonUtil.toJson(
                                                ChatSendMessageDto(
                                                    chatRoom.id,
                                                    sender.id,
                                                    message.content,
                                                    message.createdAt,
                                                    message.type,
                                                ),
                                            ),
                                        ),
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        executor,
                    )
            }
        }
        return message
    }
}
