package com.template.states

import com.template.contracts.CarContract
import com.template.contracts.TemplateContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

// *********
// * State *
// *********

@BelongsToContract(CarContract::class)
data class CarState(
    val owningBank: Party,
    val holdingDealer: Party,
    val manufacturer: Party,
    val vin: String,
    val licensePlateNumber: String,
    val make: String,
    val model: String,
    val dealershipLocation: String,
    override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState {
    override val participants: List<AbstractParty> = listOf(owningBank, holdingDealer, manufacturer)

    fun updateTradeStatus(vin: String): String {
        return vin
    }
}
// Replace TemplateState's definition with:
//class IOUState(val value: Int,
//               val lender: Party,
//               val borrower: Party) : ContractState {
//    override val participants get() = listOf(lender, borrower)
//}
