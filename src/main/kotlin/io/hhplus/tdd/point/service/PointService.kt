package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointService(private val userPointTable: UserPointTable) {

    fun getUserPoint(
        userPointId: Long,
    ) = userPointTable.selectById(userPointId)
}