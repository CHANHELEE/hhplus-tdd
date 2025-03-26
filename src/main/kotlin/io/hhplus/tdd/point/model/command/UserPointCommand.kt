package io.hhplus.tdd.point.model.command

import io.hhplus.tdd.point.model.request.UserPointRequest

class UserPointCommand {

    data class Charge(
        val amount: Long,
    )

    companion object {

        fun UserPointRequest.Charge.toCommand(): Charge =
            Charge(amount)
    }
}