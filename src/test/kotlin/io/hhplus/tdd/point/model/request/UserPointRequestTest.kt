package io.hhplus.tdd.point.model.request

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserPointRequestTest {

    @Nested
    inner class Charge {
        @Test
        fun `충전 포인트 값이 최소값 이상이면 인스턴스 생성에 성공한다`() {

            val request = UserPointRequest.Charge(UserPointRequest.Charge.MIN)
            assertEquals(UserPointRequest.Charge.MIN, request.amount)
        }

        @Test
        fun `충전 포인트 값이 최소값 미만이면 IllegalArgumentException 예외가 발생한다`() {

            assertThrows<IllegalArgumentException> {
                UserPointRequest.Charge(UserPointRequest.Charge.MIN - 1)
            }
        }

    }
}
