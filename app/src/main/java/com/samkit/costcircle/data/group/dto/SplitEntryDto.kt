package com.samkit.costcircle.data.group.dto

import kotlinx.serialization.Serializable

@Serializable
data class SplitEntryDto(
    val userId: Long,
    val value: Double? = null // Nullable because EQUAL split doesn't need a value
)