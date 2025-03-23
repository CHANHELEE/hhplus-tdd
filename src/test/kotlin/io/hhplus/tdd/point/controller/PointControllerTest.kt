package io.hhplus.tdd.point.controller

import io.hhplus.tdd.util.CommonControllerTest
import io.hhplus.tdd.util.fixture.PointHistoryFixture
import io.hhplus.tdd.util.fixture.UserPointFixture
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
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
        val userPointHistories = PointHistoryFixture.get()
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
}