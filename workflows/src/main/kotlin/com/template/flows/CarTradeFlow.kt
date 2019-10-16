package com.abc.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.CarContract
import com.template.states.CarState
import com.google.common.collect.ImmutableList
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class TradeSignedFlow(var tradeId:String,var party1:Party,var party2:Party):FlowLogic<String>() {

    @Suspendable
    override fun call(): String {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val linearId = UniqueIdentifier.Companion.fromString(tradeId)
        val queryCritera = QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.id))
        val inputStateandRef = serviceHub.vaultService.queryBy(CarState::class.java, queryCritera).states.get(0)
        val outputState = inputStateandRef.state.data
        val outputTradeState = outputState.updateTradeStatus("abc1234")
        val partyList = arrayListOf(party1,party2)
        val statusUpdationtxBuilder = TransactionBuilder(notary)
        statusUpdationtxBuilder.addInputState(inputStateandRef)
            .addOutputState( outputState, CarContract.ID)
            .addCommand(CarContract.Commands.Issue(), ourIdentity.owningKey)

        return "Trade Signed"

    }
}
