package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.util.fixture.PointHistoryFixture
import io.hhplus.tdd.util.fixture.UserPointChargeCommandFixture
import io.hhplus.tdd.util.fixture.UserPointFixture
import io.hhplus.tdd.util.fixture.UserPointUseCommandFixture
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class PointServiceIntegrationTest(
    @Autowired private val pointService: PointService,
    @Autowired private val userPointTable: UserPointTable,
    @Autowired private val pointHistoryTable: PointHistoryTable,
) {

    @BeforeEach
    fun setUp() {

        //PointHistoryTable reflection
        val historyTableField = PointHistoryTable::class.java.getDeclaredField("table")
        historyTableField.isAccessible = true
        (historyTableField.get(pointHistoryTable) as MutableList<*>).clear()

        val cursorField = PointHistoryTable::class.java.getDeclaredField("cursor")
        cursorField.isAccessible = true
        cursorField.set(pointHistoryTable, 1L)

        //UserPointTable reflection
        val userPointTableField = UserPointTable::class.java.getDeclaredField("table")
        userPointTableField.isAccessible = true
        (userPointTableField.get(userPointTable) as MutableMap<*, *>).clear()

    }

    @Test
    fun `사용자 포인트 조회에 성공한다`() {

        //given
        val userPoint = UserPointFixture.get()
        userPointTable.insertOrUpdate(userPoint.id, userPoint.point)

        //when
        val result = pointService.getUserPoint(userPoint.id)

        //then
        assertThat(result).isNotNull
        assertThat(result.point).isEqualTo(userPoint.point)

    }

    @Test
    fun `사용자 포인트 내역 조회에 성공한다`() {

        //given
        val userPoint = UserPointFixture.get()
        val userPointHistory = PointHistoryFixture.get(amount = 100L)
        pointHistoryTable.insert(
            userPoint.id,
            userPointHistory.amount,
            userPointHistory.type,
            userPointHistory.timeMillis
        )

        //when
        val result = pointService.getUserPointHistories(userPoint.id)

        //then
        assertThat(result)
            .hasSize(1)
            .extracting("id", "userId", "type", "amount")
            .containsExactlyInAnyOrder(
                Assertions.tuple(
                    result[0].id,
                    result[0].userId,
                    result[0].type,
                    result[0].amount,
                )
            )

    }

    @Test
    fun `사용자 포인트 충전 및 내역 저장에 성공한다`() {

        //given
        val userPointChargeCommand = UserPointChargeCommandFixture.get()
        val userPoint = UserPointFixture.get()
        userPointTable.insertOrUpdate(userPoint.id, userPoint.point)


        //when
        val result = pointService.charge(userPoint.id, userPointChargeCommand)
        val userPointHistoriesResult = pointHistoryTable.selectAllByUserId(userPoint.id)

        //then
        assertThat(result)
            .extracting("id", "point")
            .contains(userPoint.id, userPoint.point + userPointChargeCommand.amount)

        assertThat(userPointHistoriesResult)
            .hasSize(1)
            .extracting("id", "userId", "type", "amount")
            .containsExactlyInAnyOrder(
                Assertions.tuple(
                    userPointHistoriesResult[0].id,
                    userPointHistoriesResult[0].userId,
                    userPointHistoriesResult[0].type,
                    userPointHistoriesResult[0].amount,
                )
            )

    }


    @Test
    fun `사용자 포인트 사용 및 내역 저장에 성공한다`() {

        //given
        val userUseChargeCommand = UserPointUseCommandFixture.get()
        val userPoint = UserPointFixture.get(point = userUseChargeCommand.amount + 1_000)
        userPointTable.insertOrUpdate(userPoint.id, userPoint.point)


        //when
        val userPointResult = pointService.use(userPoint.id, userUseChargeCommand)
        val userPointHistoriesResult = pointHistoryTable.selectAllByUserId(userPoint.id)

        //then
        assertThat(userPointResult)
            .extracting("id", "point")
            .contains(userPoint.id, userPoint.point - userUseChargeCommand.amount)

        assertThat(userPointHistoriesResult)
            .hasSize(1)
            .extracting("id", "userId", "type", "amount")
            .containsExactlyInAnyOrder(
                Assertions.tuple(
                    userPointHistoriesResult[0].id,
                    userPointHistoriesResult[0].userId,
                    userPointHistoriesResult[0].type,
                    userPointHistoriesResult[0].amount,
                )
            )

    }

    //동시성 제어를 위한 테스트이므로 충전에 대한 동시성 테스트는 진행하지 않음.
    @Test
    fun `특정 사용자 포인트 사용에 대해 race condition이 제어된다 (동시성 테스트)`() {

        //given
        var userPoint = UserPointFixture.get()
        userPoint = userPointTable.insertOrUpdate(userPoint.id, userPoint.point)
        val usingPoint = 1_000L
        val userUseChargeCommands =
            listOf(
                UserPointUseCommandFixture.get(amount = usingPoint),
                UserPointUseCommandFixture.get(amount = usingPoint),
                UserPointUseCommandFixture.get(amount = usingPoint),
                UserPointUseCommandFixture.get(amount = usingPoint),
                UserPointUseCommandFixture.get(amount = usingPoint),
                UserPointUseCommandFixture.get(amount = usingPoint),
            )
        val latch = CountDownLatch(userUseChargeCommands.size)
        val executor = Executors.newFixedThreadPool(userUseChargeCommands.size)
        lateinit var usedUserPoint: UserPoint

        //when
        userUseChargeCommands.forEachIndexed { index, userPointUseCommand ->
            executor.submit {
                usedUserPoint = pointService.use(userPoint.id, userPointUseCommand)
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()

        //then
        assertThat(usedUserPoint)
            .extracting("id", "point")
            .contains(userPoint.id, userPoint.point - usingPoint * userUseChargeCommands.size)

        assertThat(pointHistoryTable.selectAllByUserId(userPoint.id))
            .hasSize(userUseChargeCommands.size)
    }

}
