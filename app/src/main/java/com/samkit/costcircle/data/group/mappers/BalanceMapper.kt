package com.samkit.costcircle.data.group.mappers

import com.samkit.costcircle.core.model.UserBalance
import com.samkit.costcircle.data.group.dto.BalanceDto

fun BalanceDto.toDomain(): UserBalance =
    UserBalance(
        userId = userId,
        name = name,
        balance = balance
    )
