package com.template.contracts

import com.template.states.CarState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class CarContract : Contract {
    companion object {
        const val ID = "com.template.contracts.CarContract"
    }

    override fun verify(tx: LedgerTransaction) {

        val command = tx.commands.requireSingleCommand<Commands>().value

        when(command) {
            is Commands.Issue -> requireThat {
                "There should be no input state" using (tx.inputs.isEmpty())
                "There should be one input state" using (tx.outputs.size == 1)
                "The output state must be of type CarState" using (tx.outputs.get(0).data is CarState)
                val outputState = tx.outputs.get(0).data as CarState
                "The licensePlateNumber must be seven characters long" using (outputState.licensePlateNumber.length == 7)
            }
        }
    }

    interface Commands : CommandData {
        class Issue : Commands
    }
}
