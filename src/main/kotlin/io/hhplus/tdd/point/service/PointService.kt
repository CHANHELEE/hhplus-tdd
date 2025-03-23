package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.model.UserPoint
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

}