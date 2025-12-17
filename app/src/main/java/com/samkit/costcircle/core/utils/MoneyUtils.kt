package com.samkit.costcircle.core.utils


import kotlin.math.abs
import kotlin.math.round

object MoneyUtils {

    fun round(amount: Double): Double =
        round(amount * 100) / 100

    fun isZero(amount: Double): Boolean =
        abs(amount) < 0.01
}
