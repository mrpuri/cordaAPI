package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountService
import com.template.commands.Create
import com.template.states.ExampleAccounts
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.transactions.TransactionBuilder
import java.util.*
/**
     * A flow to create a new account. The flow will fail if an account already exists with the provided [name] or [identifier].
     *
     * @property name the proposed name for the new account.
     * @property identifier the proposed identifier for the new account.
     */
    @StartableByService
    @StartableByRPC
    class CreateAccount private constructor(
        private val name: String,
        private val identifier: UUID
    ) : FlowLogic<StateAndRef<ExampleAccounts>>() {

        /** Create a new account with a specified [name] but generate a new random [id]. */
        constructor(name: String) : this(name, UUID.randomUUID())

        @Suspendable
        override fun call(): StateAndRef<ExampleAccounts> {
            // There might be another account on this node with the same name... That's OK as long as the host is another
            // node. This can happen because another node shared that account with us. However, there cannot be two accounts
            // with the same name with the same host node.
            require(accountService.accountInfo(name).none { it.state.data.host == ourIdentity }) {
                "There is already an account registered with the specified name $name."
            }
            require(accountService.accountInfo(identifier) == null) {
                "There is already an account registered with the specified identifier $identifier."
            }
            val notary = serviceHub.networkMapCache.notaryIdentities.first()
            val newAccountInfo = ExampleAccounts(
                name = name,
                host = ourIdentity,
                identifier = UniqueIdentifier(id = identifier)
            )
            val transactionBuilder = TransactionBuilder(notary = notary).apply {
                addOutputState(newAccountInfo)
                addCommand(Create(), ourIdentity.owningKey)
            }
            val signedTransaction = serviceHub.signInitialTransaction(transactionBuilder)
            val finalisedTransaction = subFlow(FinalityFlow(signedTransaction, emptyList()))
            return finalisedTransaction.coreTransaction.outRefsOfType<ExampleAccounts>().single()
        }
    }

