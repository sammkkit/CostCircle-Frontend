package com.samkit.costcircle.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequest(
    val idToken: String
)