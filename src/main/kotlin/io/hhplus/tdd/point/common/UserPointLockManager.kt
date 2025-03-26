package io.hhplus.tdd.point.common

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock


object UserPointLockManager {

    private val locks = ConcurrentHashMap<Long, ReentrantLock>()

    fun <T> withLock(key: Long, action: () -> T): T {

        val lock = locks.computeIfAbsent(key) { ReentrantLock(true) }

        val locked = lock.tryLock(5, TimeUnit.SECONDS)
        if (!locked) {
            error("사용자 포인트 처리중에 있습니다.")
        }

        return try {
            action()
        } finally {
            lock.unlock()
        }

    }
}