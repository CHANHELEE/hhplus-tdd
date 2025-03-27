package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.util.fixture.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {

    @InjectMocks
    private lateinit var pointService: PointService

    @Mock
    private lateinit var userPointTable: UserPointTable

    @Mock
    private lateinit var pointHistoryTable: PointHistoryTable

    @Test
    fun `사용자의 포인트를 조회한다`() {

        //given
        val userPoint = UserPointFixture.get()
        given(userPointTable.selectById(1L)).willReturn(userPoint)

        //when
        val returnedUserPoint = pointService.getUserPoint(1L)

        //then
        assertThat(returnedUserPoint)
            .extracting("id", "point")
            .contains(userPoint.id, userPoint.point)
    }

    @Test
    fun `사용자의 포인트 이용 내역을 조회한다`() {

        //given
        val userPointHistories = listOf(PointHistoryFixture.get())
        given(pointHistoryTable.selectAllByUserId(1L)).willReturn(userPointHistories)

        //when
        val returnedUserPointHistories = pointService.getUserPointHistories(1L)

        //then
        assertThat(returnedUserPointHistories)
            .extracting("id", "userId", "type", "amount")
            .containsExactlyInAnyOrder(
                tuple(
                    userPointHistories[0].id,
                    userPointHistories[0].userId,
                    userPointHistories[0].type,
                    userPointHistories[0].amount,
                )
            )
    }

    @Test
    fun `사용자 포인트를 충전한다`() {

        //given
        val requestUserId = 1L
        val pointChargeCommand = UserPointChargeCommandFixture.get()
        val userPoint = UserPointFixture.get()
        val chargedUserPoint = UserPointFixture.get(point = userPoint.point + pointChargeCommand.amount)
        val userPointHistories = PointHistoryFixture.get()

        given(userPointTable.selectById(requestUserId))
            .willReturn(userPoint)

        given(userPointTable.insertOrUpdate(requestUserId, chargedUserPoint.point))
            .willReturn(chargedUserPoint)

        given(
            pointHistoryTable.insert(
                requestUserId,
                pointChargeCommand.amount,
                TransactionType.CHARGE,
                chargedUserPoint.updateMillis
            )
        ).willReturn(userPointHistories)

        //when
        val returnedUserPoint = pointService.charge(requestUserId, pointChargeCommand)

        //then
        assertThat(returnedUserPoint)
            .extracting("id", "point")
            .contains(chargedUserPoint.id, chargedUserPoint.point)

        verify(userPointTable, times(1)).selectById(requestUserId)
        verify(userPointTable, times(1)).insertOrUpdate(requestUserId, chargedUserPoint.point)
        verify(pointHistoryTable, times(1)).insert(
            requestUserId,
            pointChargeCommand.amount,
            TransactionType.CHARGE,
            chargedUserPoint.updateMillis
        )

    }

    @Test
    fun `사용자 포인트를 시용한다`() {

        //given
        val requestUserId = 1L
        val pointUseCommand = UserPointUseCommandFixture.get()
        val userPoint = UserPointFixture.get()
        val usedUserPoint = UserPointFixture.get(point = userPoint.point - pointUseCommand.amount)
        val userPointHistories = PointHistoryFixture.get()

        given(userPointTable.selectById(requestUserId))
            .willReturn(userPoint)

        given(userPointTable.insertOrUpdate(requestUserId, usedUserPoint.point))
            .willReturn(usedUserPoint)

        given(
            pointHistoryTable.insert(
                requestUserId,
                pointUseCommand.amount,
                TransactionType.USE,
                usedUserPoint.updateMillis
            )
        ).willReturn(userPointHistories)

        //when
        val returnedUserPoint = pointService.use(requestUserId, pointUseCommand)

        //then
        assertThat(returnedUserPoint)
            .extracting("id", "point")
            .contains(usedUserPoint.id, usedUserPoint.point)

        verify(userPointTable, times(1)).selectById(requestUserId)
        verify(userPointTable, times(1)).insertOrUpdate(requestUserId, usedUserPoint.point)
        verify(pointHistoryTable, times(1)).insert(
            requestUserId,
            pointUseCommand.amount,
            TransactionType.USE,
            usedUserPoint.updateMillis
        )

    }
}