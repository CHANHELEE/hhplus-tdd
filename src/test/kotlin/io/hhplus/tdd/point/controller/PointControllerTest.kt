package io.hhplus.tdd.point.controller

import io.hhplus.tdd.point.model.command.UserPointCommand.Companion.toCommand
import io.hhplus.tdd.point.model.request.UserPointRequest
import io.hhplus.tdd.util.CommonControllerTest
import io.hhplus.tdd.util.fixture.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(PointController::class)
class PointControllerTest : CommonControllerTest() {

    @Test
    fun `사용자의 포인트 조회 API`() {

        // given
        val userPoint = UserPointFixture.get()
        given(pointService.getUserPoint(1L)).willReturn(userPoint)

        // when && then
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/point/{userPointId}", 1L)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userPoint.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(userPoint.point))
    }

    @Test
    fun `사용자의 포인트 이용 내역 조회 API`() {

        // given
        val userPointHistories = listOf( PointHistoryFixture.get())
        given(pointService.getUserPointHistories(1L)).willReturn(userPointHistories)

        // when && then
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/point/{userId}/histories", 1L)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(userPointHistories[0].id))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(userPointHistories[0].userId))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].type").value(userPointHistories[0].type.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value(userPointHistories[0].amount))
    }


    @Nested
    inner class Charge {

        @Test
        fun `사용자의 포인트 충전 API - 충전에 성공한다`() {

            // given
            val userPoint = UserPointFixture.get()
            val pointChargeRequest = UserPointChargeRequestFixture.get()
            val requestBody = objectMapper.writeValueAsString(pointChargeRequest)
            given(pointService.charge(1L, pointChargeRequest.toCommand())).willReturn(userPoint)

            // when && then
            mockMvc
                .perform(
                    MockMvcRequestBuilders.patch(
                        "/point/{userId}/charge", 1L
                    )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userPoint.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(userPoint.point))

            verify(pointService, times(1)).charge(1L, pointChargeRequest.toCommand())
        }

        @Test
        fun `사용자의 포인트 충전 API - 최소 값 미만의 포인트 충전으로 실패한다 - BadRequest`() {

            // given
            val inputPoint = UserPointRequest.Charge.MIN - 1
            val requestBody = objectMapper.writeValueAsString(mapOf("amount" to inputPoint))

            // when && then
            mockMvc
                .perform(
                    MockMvcRequestBuilders.patch(
                        "/point/{userId}/charge", 1L
                    )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
        }
    }

    @Nested
    inner class Use {

        @Test
        fun `사용자의 포인트 사용 API - 사용에 성공한다`() {

            // given
            val userPoint = UserPointFixture.get()
            val pointUseRequest = UserPointUseRequestFixture.get()
            val requestBody = objectMapper.writeValueAsString(pointUseRequest)
            given(pointService.use(userPoint.id, pointUseRequest.toCommand())).willReturn(userPoint)

            // when && then
            mockMvc
                .perform(
                    MockMvcRequestBuilders.patch(
                        "/point/{userId}/use", userPoint.id
                    )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userPoint.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(userPoint.point))

            verify(pointService, times(1)).use(userPoint.id, pointUseRequest.toCommand())
        }

        @Test
        fun `사용자의 포인트 사용 API - 최소 값 미만의 포인트 사용으로 실패한다 - BadRequest`() {

            // given
            val inputPoint = UserPointRequest.Use.MIN - 1
            val requestBody = objectMapper.writeValueAsString(mapOf("amount" to inputPoint))

            // when && then
            mockMvc
                .perform(
                    MockMvcRequestBuilders.patch(
                        "/point/{userId}/use", 1L
                    )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
        }
    }
}