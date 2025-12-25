package com.samkit.costcircle.data.auth.dto

import kotlinx.serialization.Serializable


@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val picture: String? = null
)