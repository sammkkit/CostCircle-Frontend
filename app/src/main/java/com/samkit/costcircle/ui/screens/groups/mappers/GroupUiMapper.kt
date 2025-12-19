package com.samkit.costcircle.ui.screens.groups.mappers

import com.samkit.costcircle.core.model.Group
import com.samkit.costcircle.ui.screens.groups.models.GroupUiModel

fun Group.toUiModel(): GroupUiModel =
    GroupUiModel(
        id = id,
        name = name,
        balance = 0.0 // until balances are added
    )
