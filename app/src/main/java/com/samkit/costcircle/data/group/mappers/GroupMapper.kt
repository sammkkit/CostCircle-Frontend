package com.samkit.costcircle.data.group.mappers

import com.samkit.costcircle.data.group.dto.GroupDto

fun GroupDto.toDomain() = GroupDto(
    id = id,
    name = name
)