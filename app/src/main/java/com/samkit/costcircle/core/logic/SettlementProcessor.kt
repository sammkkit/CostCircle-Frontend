package com.samkit.costcircle.core.logic


import com.samkit.costcircle.core.model.Settlement
import com.samkit.costcircle.core.model.SettlementSummary
import com.samkit.costcircle.core.model.UserBalance
import com.samkit.costcircle.core.utils.MoneyUtils

object SettlementProcessor {

    /**
     * Groups settlements per user and attaches net balance.
     */
    fun buildSummaries(
        balances: List<UserBalance>,
        settlements: List<Settlement>
    ): List<SettlementSummary> {

        val settlementsByUser = settlements.groupBy {
            it.fromUserId
        }

        return balances.map { balance ->
            SettlementSummary(
                userId = balance.userId,
                netAmount = MoneyUtils.round(balance.balance),
                settlements = settlementsByUser[balance.userId].orEmpty()
            )
        }
    }

    /**
     * Sanity check: total owed == total received
     */
    fun isSettlementValid(settlements: List<Settlement>): Boolean {
        val totalSent = settlements.sumOf { it.amount }
        return MoneyUtils.isZero(totalSent)
    }
}
