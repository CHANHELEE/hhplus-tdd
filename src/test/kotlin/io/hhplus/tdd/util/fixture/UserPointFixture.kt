package io.hhplus.tdd.util.fixture

import io.hhplus.tdd.point.model.UserPoint

object UserPointFixture {

    fun getDummyUserPoint(
        id: Long = 1L,
        point: Long = 10000L,
        updateMillis: Long = System.currentTimeMillis()
    ) = UserPoint(id, point, updateMillis)

}