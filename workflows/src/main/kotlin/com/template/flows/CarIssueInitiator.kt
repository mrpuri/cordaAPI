package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.CarContract
import com.template.states.CarState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class CarIssueInitiator(
    val owningBank: Party,
    val holdingDealer: Party,
    val manufacturer: Party,
    val vin: String,
    val licensePlateNumber: String,
    val make: String,
    val model: String,
    val dealershipLocation: String

) : FlowLogic<String>() {
    @Suspendable
    override fun call(): String {

println("call function")
        val carState = CarState(
            owningBank,
            holdingDealer,
            manufacturer,
            vin,
            licensePlateNumber,
            make,
            model,
            dealershipLocation
        )
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        //val iouState = CarState(manufacturer, serviceHub.myInfo.legalIdentities.first(), holdingDealer)
        val command = Command(CarContract.Commands.Issue(),ourIdentity.owningKey)

        val txBuilder = TransactionBuilder(notary)
            .addOutputState(carState, CarContract.ID)
            .addCommand(command)
println("step 4")
//        txBuilder.verify(serviceHub)
//        val tx = serviceHub.signInitialTransaction(txBuilder)
        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)
        println("step 5")
        val flowSessionSet = HashSet<FlowSession>()
//        val session = initiateFlow(manufacturer)
        val session1 = initiateFlow(holdingDealer)
        val session2 = initiateFlow(owningBank)
//        flowSessionSet.add(session)
        flowSessionSet.add(session1)
        flowSessionSet.add(session2)
        println("step 6")
        val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, flowSessionSet))
        println("step 7")
        // progressTracker.currentStep = FINALISING_TRANSACTION
        val txn = subFlow(FinalityFlow(fullySignedTx, session1, session2))
        println("step 8")
        return txn.coreTransaction.outputsOfType(CarState::class.java).get(0).linearId.toString()
    }
}
