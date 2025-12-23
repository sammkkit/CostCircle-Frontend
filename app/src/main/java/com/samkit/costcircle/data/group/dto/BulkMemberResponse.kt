package com.samkit.costcircle.data.group.dto

data class BulkMemberResponse(
    val msg: String,
    val addedCount:Int = 0,
    val missingEmails: List<String>
)