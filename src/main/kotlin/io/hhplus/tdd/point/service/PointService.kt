package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.TransactionType
import io.hhplus.tdd.point.model.UserPoint
import io.hhplus.tdd.point.model.command.UserPointCommand
import org.springframework.stereotype.Service

@Service
class PointService(
    private val userPointTable: UserPointTable,
    private val userPointHistoryTable: PointHistoryTable,
) {

    fun getUserPoint(
        userPointId: Long,
    ): UserPoint = userPointTable.selectById(userPointId)

    fun getUserPointHistories(
        userId: Long,
    ): List<PointHistory> = userPointHistoryTable.selectAllByUserId(userId)

    fun charge(
        userId: Long,
        userPointCommand: UserPointCommand.Charge,
    ): UserPoint {

        val userPoint = userPointTable.selectById(userId)

        var chargedUserPoint = userPoint.charge(userPointCommand.amount)
        chargedUserPoint = userPointTable.insertOrUpdate(userId, chargedUserPoint.point)

        userPointHistoryTable.insert(userId, userPointCommand.amount, TransactionType.CHARGE, chargedUserPoint.updateMillis)
        return chargedUserPoint
    }
}