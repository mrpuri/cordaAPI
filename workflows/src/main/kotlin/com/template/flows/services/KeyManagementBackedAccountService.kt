package com.template.flows.services

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.accounts.workflows.accountBaseCriteria
import com.r3.corda.lib.accounts.workflows.accountHostCriteria
import com.r3.corda.lib.accounts.workflows.accountUUIDCriteria
import com.r3.corda.lib.accounts.workflows.ourIdentity

import com.template.flows.Utilities.*
import com.template.flows.CreateAccount
import com.template.states.ExampleAccounts
import net.corda.core.concurrent.CordaFuture
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.internal.concurrent.asCordaFuture
import net.corda.core.internal.concurrent.doneFuture
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.*
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.core.utilities.contextLogger
import java.security.PublicKey
import java.util.*
import java.util.concurrent.CompletableFuture

class KeyManagementBackedAccountService {

    @CordaService
    class KeyManagementBackedAccountService(val services: AppServiceHub) : AccountService, SingletonSerializeAsToken() {
        override fun accountKeys(id: UUID): List<PublicKey> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun accountIdForKey(owningKey: PublicKey): UUID? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun shareAccountInfoWithParty(accountId: UUID, party: Party): CordaFuture<Unit> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun <T : ContractState> shareStateWithAccount(accountId: UUID, state: StateAndRef<T>): CordaFuture<Unit> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        companion object {
            val logger = contextLogger()
        }

        override fun accountsForHost(host: Party): List<StateAndRef<ExampleAccounts>> {
            return services.vaultService.queryBy<ExampleAccounts>(accountBaseCriteria.and(accountHostCriteria(host))).states
        }

        override fun ourAccounts(): List<StateAndRef<ExampleAccounts>> {
            return accountsForHost(services.ourIdentity)
        }

        override fun allAccounts(): List<StateAndRef<ExampleAccounts>> {
            return services.vaultService.queryBy<ExampleAccounts>(accountBaseCriteria).states
        }

        override fun accountInfo(id: UUID): StateAndRef<ExampleAccounts>? {
            val uuidCriteria = accountUUIDCriteria(id)
            return services.vaultService.queryBy<ExampleAccounts>(accountBaseCriteria.and(uuidCriteria)).states.singleOrNull()
        }

        override fun accountInfo(name: String): List<StateAndRef<ExampleAccounts>> {
            val nameCriteria = accountNameCriteria(name)
            val results = services.vaultService.queryBy<ExampleAccounts>(accountBaseCriteria.and(nameCriteria)).states
            return when (results.size) {
                0 -> emptyList()
                1 -> listOf(results.single())
                else -> {
                    logger.warn("WARNING: Querying for account by name returned more than one account, this is likely " +
                        "because another node shared an account with this node that has the same name as an " +
                        "account already created on this node. Filtering the results by host will allow you to access" +
                        "the AccountInfo you need.")
                    results
                }
            }
        }

        @Suspendable
        override fun createAccount(name: String): CordaFuture<StateAndRef<ExampleAccounts>> {
            return flowAwareStartFlow(CreateAccount(name))
        }

        @Suspendable
        override fun <T : StateAndRef<*>> shareStateAndSyncAccounts(state: T, party: Party): CordaFuture<Unit> {
            return flowAwareStartFlow(ShareStateAndSyncAccounts(state, party))
        }
//
//        override fun accountKeys(id: UUID): List<PublicKey> {
//            return services.identityService.publicKeysForExternalId(id).toList()
//        }
//
//        override fun accountIdForKey(owningKey: PublicKey): UUID? {
//            return services.identityService.externalIdForPublicKey(owningKey)
//        }

        override fun accountInfo(owningKey: PublicKey): StateAndRef<ExampleAccounts>? {
            return accountIdForKey(owningKey)?.let { accountInfo(it) }
        }

//        @Suspendable
//        override fun shareAccountInfoWithParty(accountId: UUID, party: Party): CordaFuture<Unit> {
//            val foundAccount = accountInfo(accountId)
//            return if (foundAccount != null) {
//                flowAwareStartFlow(ShareAccountInfo(foundAccount, listOf(party)))
//            } else {
//                CompletableFuture<Unit>().also {
//                    it.completeExceptionally(IllegalStateException("Account: $accountId was not found on this node"))
//                }.asCordaFuture()
//            }
//        }
//
//        @Suspendable
//        override fun <T : ContractState> shareStateWithAccount(accountId: UUID, state: StateAndRef<T>): CordaFuture<Unit> {
//            val foundAccount = accountInfo(accountId)
//            return if (foundAccount != null) {
//                flowAwareStartFlow(ShareStateWithAccount(accountInfo = foundAccount.state.data, state = state))
//            } else {
//                CompletableFuture<Unit>().also {
//                    it.completeExceptionally(IllegalStateException("Account: $accountId was not found on this node"))
//                }.asCordaFuture()
//            }
//        }

        @Suspendable
        private inline fun <reified T : Any> flowAwareStartFlow(flowLogic: FlowLogic<T>): CordaFuture<T> {
            val currentFlow = FlowLogic.currentTopLevel
            return if (currentFlow != null) {
                val result = currentFlow.subFlow(flowLogic)
                doneFuture(result)
            } else {
                this.services.startFlow(flowLogic).returnValue
            }
        }

    }
}
