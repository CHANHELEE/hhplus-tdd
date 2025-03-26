package io.hhplus.tdd.point.model

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {

    init {
        require(point in MIN..MAX) {
            throw IllegalArgumentException("포인트 저장 값은 $MIN ~  $MAX 사이의 값이 필요합니다.")
        }
    }

    companion object {
        const val MIN = 0L
        const val MAX = 1_000_000L
    }

    fun charge(amount:Long): UserPoint {

        val chargedPoint = point + amount
        require(chargedPoint <= MAX) {
            throw IllegalArgumentException("포인트 충전은 $MAX 를 넘을 수 없습니다.")
        }
        return UserPoint(id, chargedPoint, updateMillis)
    }

}

