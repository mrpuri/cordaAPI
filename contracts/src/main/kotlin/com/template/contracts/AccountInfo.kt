package com.template.contracts


import com.template.commands.AccountCommand
import com.template.commands.Create
import com.template.states.ExampleAccounts
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.transactions.LedgerTransaction
class AccountInfo: Contract {
    override fun verify(tx: LedgerTransaction) {
        val accountCommand = tx.commands.requireSingleCommand(AccountCommand::class.java)
        if (accountCommand.value is Create) {
            require(tx.outputStates.size == 1) { "There should only ever be one output account state." }
            val accountInfo = tx.outputsOfType(ExampleAccounts::class.java).single()
            val requiredSigners = accountCommand.signers
            require(requiredSigners.size == 1) { "There should only be one required signer for opening an account." }
            require(requiredSigners.single() == accountInfo.host.owningKey) {
                "Only the hosting node should be able to sign."
            }
        } else {
            throw NotImplementedError()
        }
    }
}
