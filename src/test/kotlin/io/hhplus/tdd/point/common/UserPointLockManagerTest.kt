package io.hhplus.tdd.point.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


class UserPointLockManagerTest {

    @Test
    fun `5 초동안 락을 획득하지 못하면 IllegalStateException 예외가 발생한다`() {

        val userId = 1L
        val executor = Executors.newSingleThreadExecutor()

        executor.submit {
            UserPointLockManager.withLock(userId) {
                Thread.sleep(6000)
                executor.shutdownNow()
            }
        }

        assertThrows<IllegalStateException> {
            UserPointLockManager.withLock(userId) {
                // 이 블록은 실행 되지 않습니다.
            }
        }
    }

    @Test
    fun `특정 사용자에 대한 Lock 적용에 성공한다`() {

        val userId = 1L
        val userCount = 2
        val latch = CountDownLatch(userCount)
        val executor = Executors.newFixedThreadPool(userCount)

        val startTime = System.currentTimeMillis()

        repeat(userCount) { index ->
            executor.submit {
                UserPointLockManager.withLock(userId) {
                    println("Thread $index: Lock 획득 - userId: $userId")
                    Thread.sleep(2000)
                    println("Thread $index: Lock 해제 - userId: $userId")
                }
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()

        val endTime = System.currentTimeMillis()

        val resultTime = endTime - startTime
        println("총 실행 시간: ${resultTime}ms")
        assertThat(resultTime).isGreaterThanOrEqualTo(2000)
    }

    @Test
    fun `2명의 사용자가 Lock을 요청 할 경우 Lock 획득에 성공한다`() {

        val userIds = listOf(1L, 2L)
        val latch = CountDownLatch(userIds.size)
        val executor = Executors.newFixedThreadPool(userIds.size)

        val startTime = System.currentTimeMillis()

        userIds.forEachIndexed { index, userId ->
            executor.submit {
                UserPointLockManager.withLock(userId) {
                    println("Thread $index: Lock 획득 - userId: $userId")
                    Thread.sleep(2000)
                    println("Thread $index: Lock 해제 - userId: $userId")
                }
                latch.countDown()
            }
        }

        latch.await()
        executor.shutdown()

        val resultTime = System.currentTimeMillis() - startTime
        println("총 실행 시간: ${resultTime}ms")

        assertThat(resultTime).isLessThan(3999)
    }
}