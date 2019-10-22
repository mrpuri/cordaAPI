package com.template.beans

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

class carRequestBean (
    val owningBank: String,
    val holdingDealer: String,
    val manufacturer: String,
    val vin: String,
    val licensePlateNumber: String,
    val make: String,
    val model: String,
    val dealershipLocation: String

)
{

}
