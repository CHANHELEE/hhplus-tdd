package io.hhplus.tdd.util.fixture

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.model.command.UserPointCommand
import io.hhplus.tdd.point.model.request.UserPointRequest

object UserPointFixture {
    fun get(
        id: Long = 1L,
        point: Long = 10000L,
        updateMillis: Long = System.currentTimeMillis()
    ): UserPoint = UserPoint(id, point, updateMillis)
}

object UserPointChargeRequestFixture {
    fun get(
        amount: Long = 10_000L,
    ): UserPointRequest.Charge = UserPointRequest.Charge(amount)
}

object UserPointUseRequestFixture {
    fun get(
        amount: Long = 10_000L,
    ): UserPointRequest.Use = UserPointRequest.Use(amount)
}

object UserPointChargeCommandFixture {
    fun get(
        amount: Long = 10_000L,
    ): UserPointCommand.Charge = UserPointCommand.Charge(amount)
}

object UserPointUseCommandFixture {
    fun get(
        amount: Long = 10_000L,
    ): UserPointCommand.Use = UserPointCommand.Use(amount)
}

object PointHistoryFixture {
    fun get(
        id: Long = 1L,
        userId: Long = 1L,
        type: TransactionType = TransactionType.USE,
        amount: Long = 10000L,
        timeMillis: Long = System.currentTimeMillis()
    ): PointHistory = PointHistory(id, userId, type, amount, timeMillis)

}