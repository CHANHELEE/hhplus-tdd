package io.hhplus.tdd.point.model.request

class UserPointRequest {

    data class Charge(
        val amount: Long,
    ) {

        init {
            require(amount >= MIN) {
                throw IllegalArgumentException("포인트 충전 값은 $MIN 이상의 값이 필요합니다.")
            }
        }

        companion object {
            const val MIN = 1L
        }
    }

    data class Use(
        val amount: Long,
    ) {

        init {
            require(amount >= MIN) {
                throw IllegalArgumentException("포인트 사용 값은 $MIN 이상의 값이 필요합니다.")
            }
        }

        companion object {
            const val MIN = 1L
        }

    }
}
