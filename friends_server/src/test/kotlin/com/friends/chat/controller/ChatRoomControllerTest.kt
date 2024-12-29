package com.friends.chat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.friends.chat.CREATE_CHAT_ROOM_REQUEST
import com.friends.chat.TEST_CHAT_ROOM_ID
import com.friends.chat.createTestChatRoomCreateRequestDto
import com.friends.chat.createTestChatRoomDetailResponseDto
import com.friends.chat.createTestMockSliceResponseChatRoom
import com.friends.chat.service.ChatRoomCommandService
import com.friends.chat.service.ChatRoomQueryService
import com.friends.support.annotation.ControllerTest
import com.friends.support.createMultipartFile
import com.friends.support.getWithAuthentication
import com.friends.support.multipartWithAuthentication
import com.friends.support.postWithAuthentication
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerTest
@WebMvcTest(ChatRoomController::class)
class ChatRoomControllerTest(
    @MockkBean private val chatRoomCommandService: ChatRoomCommandService,
    @MockkBean private val chatRoomQueryService: ChatRoomQueryService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec({
        val requestPath = "/api/user/chat/room"

        given("POST $requestPath Test") {
            `when`("정상적인 요청이 들어올 경우") {
                val request = createTestChatRoomCreateRequestDto()
                every { chatRoomCommandService.createChatRoom(any(), any(), any()) } returns TEST_CHAT_ROOM_ID
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
                val request = createTestChatRoomCreateRequestDto(categories = setOf())
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

        given("GET $requestPath Test") {
            `when`("정상적인 요청이 들어올 경우") {
                every { chatRoomQueryService.getChatRooms(any(), any(), any(), any()) } returns createTestMockSliceResponseChatRoom()
                then("채팅방을 조회한다.") {
                    mockMvc
                        .perform(
                            getWithAuthentication(requestPath),
                        ).andExpect(
                            status().isOk,
                        )
                }
            }

            `when`("사이즈가 양수가 아닌 경우") {
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            getWithAuthentication(requestPath).param("size", "0"),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }

            `when`("lastChatRoomMemberId가 양수가 아닌 경우") {
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            getWithAuthentication(requestPath).param("lastChatRoomMemberId", "-1"),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }
        }

        given("GET  $requestPath/{id} Test") {
            `when`("정상적인 요청이 들어온 경우") {
                every { chatRoomQueryService.getChatRoomDetail(any(), any()) } returns createTestChatRoomDetailResponseDto()
                then("채팅방 상세를 조회한다,") {
                    mockMvc
                        .perform(
                            getWithAuthentication("$requestPath/$TEST_CHAT_ROOM_ID"),
                        ).andExpect(
                            status().isOk,
                        )
                }
            }

            `when`("채팅방 ID가 양수가 아닌 경우") {
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            getWithAuthentication("$requestPath/0"),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }
        }

        given("POST $requestPath/{chatRoomId} Test") {
            `when`("정상적인 요청이 들어올 경우") {
                every { chatRoomCommandService.enterChatRoom(any(), any()) } returns Unit
                then("채팅방에 입장한다.") {
                    mockMvc
                        .perform(
                            postWithAuthentication("$requestPath/$TEST_CHAT_ROOM_ID"),
                        ).andExpect(
                            status().isNoContent,
                        )
                }
            }

            `when`("채팅방 ID가 0인 경우") {
                then("400 에러 발생") {
                    mockMvc
                        .perform(
                            postWithAuthentication("$requestPath/0"),
                        ).andExpect(
                            status().isBadRequest,
                        )
                }
            }
        }
    })
