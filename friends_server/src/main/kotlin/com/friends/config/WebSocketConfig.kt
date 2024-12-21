package com.friends.config

import com.friends.chat.websocket.ChatWebSocketHandler
import com.friends.chat.websocket.ChatWebsocketInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatWebSocketHandler: ChatWebSocketHandler,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(chatWebSocketHandler, "/ws/chatRoom/{chatRoomId}")
            .addInterceptors(ChatWebsocketInterceptor())
            .setAllowedOrigins("*") // CORS 허용
    }
}
