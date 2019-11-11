package com.template.flows

import com.r3.corda.lib.accounts.workflows.internal.schemas.AllowedToSeeStateMapping
import com.template.flows.services.AccountService
import com.template.flows.services.KeyManagementBackedAccountService
import com.template.schemas.AccountSchemas
import com.template.states.ExampleAccounts
import net.corda.core.CordaInternal
import net.corda.core.contracts.ContractState
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.Builder.`in`
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.*
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import java.util.*

class Utilities {

    /** Helper for obtaining a [AccountService]. */
    val FlowLogic<*>.accountService: AccountService
        get() = serviceHub.cordaService( KeyManagementBackedAccountService::class.java)

    // TODO: Remove this and replace with a utility in a commons CorDapp.
    val ServiceHub.ourIdentity get() = myInfo.legalIdentities.first()

// Query utilities.

    /** Returns the base [ExampleAccounts] query criteria. */
    val accountBaseCriteria = QueryCriteria.VaultQueryCriteria(
        contractStateTypes = setOf(ExampleAccounts::class.java),
        status = Vault.StateStatus.UNCONSUMED
    )

    /** To query [ExampleAccounts]s by host. */
    fun accountHostCriteria(host: Party): QueryCriteria {
        return builder {
            val partySelector = AccountSchemas.PersistentAccountInfo::host.equal(host)
            QueryCriteria.VaultCustomQueryCriteria(partySelector)
        }
    }

    /** To query [ExampleAccounts]s by name. */
    fun accountNameCriteria(name: String): QueryCriteria {
        return builder {
            val nameSelector = AccountSchemas.PersistentAccountInfo::name.equal(name)
            QueryCriteria.VaultCustomQueryCriteria(nameSelector)
        }
    }

    /** To query [ExampleAccounts]s by id. */
    fun accountUUIDCriteria(id: UUID): QueryCriteria {
        return builder {
            val idSelector = AccountSchemas.PersistentAccountInfo::id.equal(id)
            QueryCriteria.VaultCustomQueryCriteria(idSelector)
        }
    }

    /** To query [ContractState]s by which an account has been allowed to see an an observer. */
    fun allowedToSeeCriteria(accountIds: List<UUID>): QueryCriteria {
        return builder {
            val allowedToSeeSelector = AllowedToSeeStateMapping::externalId.`in`(accountIds)
            QueryCriteria.VaultCustomQueryCriteria(allowedToSeeSelector)
        }
    }

    /**
     * This only works on Corda 4 if both the "allow to see" table and the "external id to state table" contains rows. This
     * is a bug due to Hibernate using CROSS JOIN instead of LEFT JOIN, the result is that if either of the tables contains
     * no rows, then the resultant query returns no rows, when some should be returned. This will be fixed in Corda 5.
     *
     * The workaround, for now, is to perform queries for states observed by an account, separate to queries for states
     * owned by an account. Some temporary utilities have been provided to help you with this. See: [accountObservedQueryBy]
     * and [accountObservedTrackBy].
     *
     * TODO: Check status of CORDA-3038 (https://r3-cev.atlassian.net/browse/CORDA-3038).
     */
//    @CordaInternal
//    fun accountQueryCriteria(accountIds: List<UUID>): QueryCriteria {
//        return allowedToSeeCriteria(accountIds).or(QueryCriteria.VaultQueryCriteria(externalIds = accountIds))
//    }

}
