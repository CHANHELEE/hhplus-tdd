package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.model.UserPoint
import org.springframework.stereotype.Service

@Service
class PointService(private val userPointTable: UserPointTable) {

    fun getUserPoint(
        userPointId: Long,
    ): UserPoint = userPointTable.selectById(userPointId)
}