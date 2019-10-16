package com.template.states
import net.corda.core.identity.Party
import com.template.contracts.TemplateContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(TemplateContract::class)
data class TemplateState(val data: String, override val participants: List<AbstractParty> = listOf()) : ContractState




// Replace TemplateState's definition with:
//class IOUState(val value: Int,
//               val lender: Party,
//               val borrower: Party) : ContractState {
//    override val participants get() = listOf(lender, borrower)
//}
