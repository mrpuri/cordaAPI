package com.template.webserver

import com.template.Utilities.CommonUtilities
import com.template.beans.CarRequestResponseBean
import com.template.beans.CreateAssetBean
import com.template.beans.carRequestBean
import net.corda.core.identity.AbstractParty
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.template.flows.CarIssueInitiator
import com.template.flows.CreateAccount
import com.template.flows.CreateAssetStateFlow
import com.template.states.Asset
import com.template.states.CarState
import net.corda.core.contracts.Amount
import net.corda.core.messaging.startFlow
import net.corda.core.utilities.getOrThrow
import java.util.*

/**
 * Define your API endpoints here.
 */
@RestController
//@CrossOrigin(origins = arrayOf("*"), allowedHeaders = arrayOf("*"))
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = "/templateendpoint", produces = arrayOf("text/plain"))
    private fun templateendpoint(): String {
        return "Define an endpoint here."
    }
    @GetMapping(value = "/custom", produces = arrayOf("text/plain"))
    private fun todo(): String {
        return "hello"
    }
    @GetMapping(value = "/getAllTrades", produces = arrayOf("application/json"))
    private fun getAllTrades():ResponseEntity<List<CarRequestResponseBean>>{
    val carList = proxy.vaultQuery(CarState::class.java).states

    return ResponseEntity.ok(CommonUtilities.convertToCarResponseBean(carList))
}
    @PostMapping(value = "/createTrade", produces = arrayOf("application/json"))
    private fun signedTradeByTrader(@RequestParam tradeId:String,@RequestParam buyer:String):String{
        val owner = proxy.nodeInfo().legalIdentities.get(0)
        println(owner)
        val surveyor = proxy.partiesFromName("BankofAmerica", false).iterator().next()

        println(surveyor)
        println(proxy.registeredFlows())


        return "Trade Signed Successfully By Seller"
    }
    @PostMapping(value = "/carTrade", produces = arrayOf("text/plain"))
    private fun signedTrade(@RequestBody carRequestBean: carRequestBean):ResponseEntity<String>{

        val Manufacturer = proxy.nodeInfo().legalIdentities.get(0)
        val Dealer = proxy.partiesFromName(carRequestBean.holdingDealer, false).iterator().next()
            ?:throw  IllegalArgumentException("No exact match found for Buyer name ${carRequestBean.holdingDealer}")


        val Bank = proxy.partiesFromName(carRequestBean.owningBank, false).singleOrNull()
            ?:throw  IllegalArgumentException("No exact match found for Surveyor name ${carRequestBean.owningBank}.")
//        val listOfListeners = ArrayList<AbstractParty>()
//        listOfListeners.add(Manufacturer)
//        listOfListeners.add(Dealer)
//        listOfListeners.add(Bank)
        val result = proxy.startFlowDynamic(CarIssueInitiator::class.java, Bank, Dealer,
            Manufacturer, carRequestBean.vin, carRequestBean.licensePlateNumber,
        carRequestBean.make, carRequestBean.model, carRequestBean.dealershipLocation).returnValue.get()



        return ResponseEntity(result, HttpStatus.CREATED)
    }

    @PostMapping(value= "/CreateAsset", produces = arrayOf("text/plain"))
    private fun CreateAsset(@RequestBody CreateAsset: CreateAssetBean): String
    {
        val result = proxy.startFlowDynamic(CreateAssetStateFlow.Initiator::class.java, CreateAsset.Cusip, CreateAsset.name, Amount.parseCurrency(CreateAsset.Ammount)).returnValue.get()
        println("Result generated")
        return result.coreTransaction.outputsOfType(Asset::class.java).get(0).assetName + " is the asset created"
    }
}


