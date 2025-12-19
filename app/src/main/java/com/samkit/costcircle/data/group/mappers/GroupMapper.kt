package com.samkit.costcircle.data.group.mappers

import com.samkit.costcircle.core.model.Group
import com.samkit.costcircle.data.group.dto.GroupDto

fun GroupDto.toDomain() = Group(
    id = id,
    name = name
)