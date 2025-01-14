package com.friends.config

import com.friends.alarm.websocket.AlarmWebsocketHandler
import com.friends.alarm.websocket.AlarmWebsocketInterceptor
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
    private val chatWebsocketInterceptor: ChatWebsocketInterceptor,
    private val alarmWebsocketHandler: AlarmWebsocketHandler,
    private val alarmWebsocketInterceptor: AlarmWebsocketInterceptor,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(chatWebSocketHandler, "/ws/chat")
            .addInterceptors(chatWebsocketInterceptor)
            .setAllowedOrigins("*") // CORS 허용

        registry
            .addHandler(alarmWebsocketHandler, "/ws/alarm")
            .addInterceptors(alarmWebsocketInterceptor)
            .setAllowedOrigins("*") // CORS 허용
    }
}
