package com.template.beans

import net.corda.core.contracts.UniqueIdentifier

class CarRequestResponseBean( val LinearId: UniqueIdentifier,
                             val vin: String,
                             val licensePlateNumber: String,
                             val make: String,
                             val model: String,
                             val dealershipLocation: String) {
}
