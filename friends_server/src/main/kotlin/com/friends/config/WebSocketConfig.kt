package com.friends.config

import com.friends.chat.websocket.ChatRoomWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatRoomWebSocketHandler: ChatRoomWebSocketHandler,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(chatRoomWebSocketHandler, "/ws/chatRoom/{chatRoomId}")
            .setAllowedOrigins("*") // CORS 허용
    }
}
