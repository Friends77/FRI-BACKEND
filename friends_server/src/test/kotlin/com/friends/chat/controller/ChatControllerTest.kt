package com.friends.chat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.friends.chat.CREATE_CHAT_ROOM_REQUEST
import com.friends.chat.createTestChatRoomCreateRequestDto
import com.friends.chat.service.ChatRoomCommandService
import com.friends.support.annotation.ControllerTest
import com.friends.support.createMultipartFile
import com.friends.support.multipartWithAuthentication
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerTest
@WebMvcTest(ChatController::class)
class ChatControllerTest(
    @MockkBean private val chatRoomCommandService: ChatRoomCommandService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec({
        val requestPath = "/api/user/chat"

        isolationMode = IsolationMode.InstancePerLeaf

        given("POST $requestPath Test") {
            `when`("정상적인 요청이 들어올 경우") {
                val request = createTestChatRoomCreateRequestDto()
                every { chatRoomCommandService.createChatRoom(any(), any(), any()) } returns Unit
                then("채팅방을 생성한다.") {
                    mockMvc
                        .perform(
                            multipartWithAuthentication(requestPath).file(createMultipartFile(CREATE_CHAT_ROOM_REQUEST, objectMapper.writeValueAsBytes(request).inputStream())),
                        ).andExpect(
                            status().isCreated,
                        )
                }
            }

            `when`("채팅방 제목이 공백인 경우") {
                val request = createTestChatRoomCreateRequestDto(title = " ")
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            multipartWithAuthentication(requestPath).file(createMultipartFile(CREATE_CHAT_ROOM_REQUEST, objectMapper.writeValueAsBytes(request).inputStream())),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }

            `when`("채팅방 제목이 0자 인 경우") {
                val request = createTestChatRoomCreateRequestDto(title = "")
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            multipartWithAuthentication(requestPath).file(createMultipartFile(CREATE_CHAT_ROOM_REQUEST, objectMapper.writeValueAsBytes(request).inputStream())),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }

            `when`("채팅방 제목이 30자 이상인 경우") {
                val request = createTestChatRoomCreateRequestDto(title = "랄".repeat(30))
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            multipartWithAuthentication(requestPath).file(createMultipartFile(CREATE_CHAT_ROOM_REQUEST, objectMapper.writeValueAsBytes(request).inputStream())),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }

            `when`("채팅방 카테고리가 없는 경우") {
                val request = createTestChatRoomCreateRequestDto(categories = mutableListOf())
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            multipartWithAuthentication(requestPath).file(createMultipartFile(CREATE_CHAT_ROOM_REQUEST, objectMapper.writeValueAsBytes(request).inputStream())),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }
        }
    })
