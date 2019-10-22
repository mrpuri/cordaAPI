package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*

@InitiatedBy(CarIssueInitiator::class)
class ResponderCarIssueInitiator(val otherPartySession:FlowSession):FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherPartySession))
    }

}
