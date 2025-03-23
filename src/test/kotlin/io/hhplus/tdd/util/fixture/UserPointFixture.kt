package io.hhplus.tdd.util.fixture

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint

object UserPointFixture {

    fun getDummyUserPoint(
        id: Long = 1L,
        point: Long = 10000L,
        updateMillis: Long = System.currentTimeMillis()
    ) = UserPoint(id, point, updateMillis)

    fun getDummyPointHistories(
        id: Long = 1L,
        userId: Long = 1L,
        type: TransactionType = TransactionType.USE,
        amount: Long = 10000L,
        timeMillis: Long = System.currentTimeMillis()
    ): List<PointHistory> = listOf(PointHistory(id, userId, type, amount, timeMillis))

}