package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.util.fixture.UserPointFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

    @InjectMocks
    private lateinit var pointService: PointService

    @Mock
    private lateinit var userPointTable: UserPointTable

    @Test
    fun `사용자의 포인트를 조회한다`() {

        //given
        val userPoint = UserPointFixture.getDummyUserPoint()
        given(userPointTable.selectById(1L)).willReturn(userPoint)

        //when
        val returnedUserPoint = pointService.getUserPoint(1L)

        //then
        assertThat(returnedUserPoint)
            .extracting("id", "point")
            .contains(userPoint.id, userPoint.point)
    }
}