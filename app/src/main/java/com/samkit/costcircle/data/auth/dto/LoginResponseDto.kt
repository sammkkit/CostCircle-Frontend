package com.samkit.costcircle.data.auth.dto

data class LoginResponseDto(
    val msg: String,
    val token: String,
    val user: UserDto
)
