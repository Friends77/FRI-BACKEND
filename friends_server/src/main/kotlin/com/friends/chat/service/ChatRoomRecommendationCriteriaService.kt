package com.friends.chat.service

import com.friends.chat.repository.ChatRoomMemberAgeRangeRepository
import com.friends.chat.repository.ChatRoomMemberGenderRepository
import com.friends.chat.repository.ChatRoomMemberRepository
import com.friends.chat.repository.ChatRoomRepository
import com.friends.profile.entity.GenderEnum
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ChatRoomRecommendationCriteriaService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomMemberRepository: ChatRoomMemberRepository,
    private val chatRoomMemberAgeRangeRepository: ChatRoomMemberAgeRangeRepository,
    private val chatRoomMemberGenderRepository: ChatRoomMemberGenderRepository,
) {
    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    fun extractMemberMostAgeRange() {
        val chatRooms = chatRoomRepository.findAll()
        val now = LocalDate.now()
        val year = now.year
        chatRooms.forEach { chatRoom ->
            val chatRoomMembers = chatRoomMemberRepository.findByChatRoom(chatRoom)
            val ageRangeCountMap = mutableMapOf<Int, Int>()
            chatRoomMembers.forEach { chatRoomMember ->
                chatRoomMember.member.profile?.birth?.also { birth ->
                    val age =
                        if (now.isAfter(birth.withYear(year))) {
                            year - birth.year
                        } else {
                            year - birth.year - 1
                        }.let {
                            if (it < 10) {
                                10
                            } else {
                                it
                            }
                        }
                    val ageRange = (age / 10) * 10
                    ageRangeCountMap[ageRange] = ageRangeCountMap.getOrDefault(ageRange, 0) + 1
                }
            }
            val maxCount = ageRangeCountMap.values.maxOrNull()
            if (maxCount != null) {
                val mostCommonAgeRanges =
                    ageRangeCountMap
                        .filter { it.value == maxCount }
                        .keys
                        .sorted()
                        .joinToString(",")
                chatRoomMemberAgeRangeRepository.save(chatRoom.id, mostCommonAgeRanges)
            }
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    fun extractMemberMostGender() {
        val chatRooms = chatRoomRepository.findAll()
        chatRooms.forEach { chatRoom ->
            val chatRoomMembers = chatRoomMemberRepository.findByChatRoom(chatRoom)
            val genderCountMap =
                chatRoomMembers
                    .filter { chatRoomMember -> chatRoomMember.member.profile?.gender == GenderEnum.MAN || chatRoomMember.member.profile?.gender == GenderEnum.WOMAN }
                    .groupingBy { it.member.profile?.gender }
                    .eachCount()
                    .toMutableMap()
            val manCount = genderCountMap.getOrDefault(GenderEnum.MAN, 0)
            val womanCount = genderCountMap.getOrDefault(GenderEnum.WOMAN, 0)
            val mostGender =
                when {
                    manCount == 0 && womanCount == 0 -> null
                    manCount > womanCount -> GenderEnum.MAN.toString()
                    womanCount > manCount -> GenderEnum.WOMAN.toString()
                    else -> "${GenderEnum.MAN},${GenderEnum.WOMAN}"
                }
            if (mostGender != null) {
                chatRoomMemberGenderRepository.save(chatRoom.id, mostGender)
            }
        }
    }
}
