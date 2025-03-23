package io.hhplus.tdd.point.controller

import io.hhplus.tdd.util.CommonControllerTest
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
        val userPoint = UserPointFixture.getDummyUserPoint()
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
}