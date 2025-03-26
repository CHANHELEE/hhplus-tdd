package io.hhplus.tdd.point.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserPointTest {

    @Test
    fun `포인트 생성 시 최소 ~ 최대값 범위에 있으면 인스턴스 생성에 성공한다`() {

        val minUserPoint = UserPoint(1L, UserPoint.MIN, System.currentTimeMillis())
        val maxUserPoint = UserPoint(1L, UserPoint.MAX, System.currentTimeMillis())

        assertEquals(UserPoint.MIN, minUserPoint.point)
        assertEquals(UserPoint.MAX, maxUserPoint.point)
    }

    @Test
    fun `포인트 생성 시 최소값 미만 최대값 초과면 예외가 발생한다 - IllegalArgumentException`() {


        assertThrows<IllegalArgumentException> {
            UserPoint(1L, UserPoint.MIN - 1, System.currentTimeMillis())
            UserPoint(1L, UserPoint.MAX + 1, System.currentTimeMillis())
        }

    }

    @Nested
    inner class Charge {

        @Test
        fun `포인트 충전에 성공한다`() {

            //given
            val userPoint = UserPoint(id = 1L, point = UserPoint.MAX - 1, updateMillis = System.currentTimeMillis())

            //when
            val chargedUserPoint = userPoint.charge(1L)

            //then
            assertEquals(UserPoint.MAX, chargedUserPoint.point)

        }

        @Test
        fun `충전된 포인트 값이 최대값을 초과하면 예외가 발생한다 - IllegalArgumentException`() {

            //given
            val userPoint = UserPoint(id = 1L, point = UserPoint.MAX, updateMillis = System.currentTimeMillis())

            //when && then
            assertThrows<IllegalArgumentException> {
                userPoint.charge(1L)
            }

        }

    }

}