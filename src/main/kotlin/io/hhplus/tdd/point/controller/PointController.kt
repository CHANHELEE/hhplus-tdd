package io.hhplus.tdd.point.controller

import io.hhplus.tdd.point.model.PointHistory
import io.hhplus.tdd.point.service.PointService
import io.hhplus.tdd.point.model.UserPoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(
    private val pointService: PointService
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("{userPointId}")
    fun point(
        @PathVariable userPointId: Long,
    ): UserPoint = pointService.getUserPoint(userPointId)

    @GetMapping("{userId}/histories")
    fun history(
        @PathVariable userId: Long,
    ): List<PointHistory> = pointService.getUserPointHistories(userId)

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        return UserPoint(0, 0, 0)
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPoint {
        return UserPoint(0, 0, 0)
    }
}