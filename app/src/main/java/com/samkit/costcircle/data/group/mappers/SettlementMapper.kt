package com.samkit.costcircle.data.group.mappers

import com.samkit.costcircle.core.model.Settlement
import com.samkit.costcircle.data.group.dto.SettlementDto

fun SettlementDto.toDomain(): Settlement =
    Settlement(
        fromUserId = fromUserId,
        toUserId = toUserId,
        amount = amount
    )