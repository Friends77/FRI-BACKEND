package com.friends.alarm.websocket

import org.springframework.stereotype.Component
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class AlarmWebsocketHandler : TextWebSocketHandler()
