package com.template.webserver

import net.corda.core.identity.AbstractParty
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

import com.template.flows.CarIssueInitiator

/**
 * Define your API endpoints here.
 */
@RestController
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
//    @PostMapping(value="/createTrade", produces = arrayOf("application/json"))
//    private fun createTrade(@RequestBody tradeRequestBean: TradeRequestBean): ResponseEntity<String> {
//
//        val owner = proxy.nodeInfo().legalIdentities.get(0)
//        val buyer = proxy.partiesFromName(tradeRequestBean.buyer, false).iterator().next()
//            ?:throw  IllegalArgumentException("No exact match found for Buyer name ${tradeRequestBean.buyer}")
//
//        val surveyor = proxy.partiesFromName("Surveyor", false).singleOrNull()
//            ?:throw  IllegalArgumentException("No exact match found for Surveyor name ${tradeRequestBean.buyer}.")
//
//
//        val listOfListeners = ArrayList<AbstractParty>()
//        listOfListeners.add(owner)
//        listOfListeners.add(buyer)
//        listOfListeners.add(surveyor)
//        var IssignedByBuyer = false
//        var IssignedBySeller = false
//        var IssignedBySurveyor = false
//        var preFinancePrice = false
//        val currentDateTime = LocalDateTime.now()
//
//        val result = proxy.startFlowDynamic(TradeRequestFlow::class.java,tradeRequestBean.typeOfCommodity,
//            tradeRequestBean.quality,tradeRequestBean.quantity,tradeRequestBean.spotMarketPricePerUnit,
//            tradeRequestBean.sellingPrice,tradeRequestBean.Unit,tradeRequestBean.referenceSpotMarket,
//            tradeRequestBean.startDate,tradeRequestBean.estimateDateofArrival,tradeRequestBean.seller,buyer,
//            surveyor,tradeRequestBean.loadMaterialCompany,tradeRequestBean.loadMaterialCost,
//            tradeRequestBean.loadMaterialEstimatedDate,tradeRequestBean.inspectMaterialCompany,
//            tradeRequestBean.inspectMaterialCost,tradeRequestBean.inspectMaterialEstimatedDate,
//            tradeRequestBean.issueExportCompany,tradeRequestBean.issueExportCost,tradeRequestBean.issueExportEstimatedDate,
//            tradeRequestBean.issueProvisionalWeightCompany,tradeRequestBean.issueProvisionalWeightCost,
//            tradeRequestBean.issueProvisionalWeightEstimatedDate,tradeRequestBean.issueProvisionalQualityCompany,
//            tradeRequestBean.issueProvisionalQualityCost,tradeRequestBean.issueProvisionalQualityEstimatedDate,
//            tradeRequestBean.truckingAtOriginCompany,tradeRequestBean.truckingAtOriginCost,
//            tradeRequestBean.truckingAtOriginEstimatedDate,tradeRequestBean.informPortOfShipmentCompany,
//            tradeRequestBean.informPortOfShipmentCost,tradeRequestBean.informPortOfShipmentEstimatedDate,
//            tradeRequestBean.allowContainertoPortCompany,tradeRequestBean.allowContainertoPortCost,
//            tradeRequestBean.allowContainertoPortEstimatedDate,tradeRequestBean.issueTruckingInsuranceCompany,
//            tradeRequestBean.issueTruckingInsuranceCost,tradeRequestBean.issueTruckingInsuranceEstimatedDate,
//            tradeRequestBean.receiverContainerInPortCompany,tradeRequestBean.receiverContainerInPortCost,
//            tradeRequestBean.receiverContainerInPortEstimatedDate,tradeRequestBean.bookingOfVesselCompany,
//            tradeRequestBean.bookingOfVesselCost,tradeRequestBean.bookingOfVesselEstimatedDate,
//            tradeRequestBean.confirmShipmentCompany,tradeRequestBean.confirmShipmentCost,
//            tradeRequestBean.confirmShipmentEstimatedDate,tradeRequestBean.issueBolCompany,
//            tradeRequestBean.issueBolCost,tradeRequestBean.issueBolEstimatedDate,
//            tradeRequestBean.issueSeaInsuranceCompany,tradeRequestBean.issueSeaInsuranceCost,
//            tradeRequestBean.issueSeaInsuranceEstimatedDate,tradeRequestBean.arrivalConfirmationCompany,
//            tradeRequestBean.arrivalConfirmationCost,tradeRequestBean.arrivalConfirmationEstimatedDate,
//            tradeRequestBean.customerCleareanceCompany,tradeRequestBean.customerCleareanceCost,
//            tradeRequestBean.customerCleareanceEstimatedDate,tradeRequestBean.truckingToCustomerCompany,
//            tradeRequestBean.truckingToCustomerCost,tradeRequestBean.truckingToCustomerEstimatedDate,
//            tradeRequestBean.arrivalConfirmationtoClientCompany,tradeRequestBean.arrivalConfirmationtoClientCost,
//            tradeRequestBean.arrivalConfirmationtoClientEstimatedDate,tradeRequestBean.dischargeOperationtoClientCompany,
//            tradeRequestBean.dischargeOperationtoClientCost,tradeRequestBean.dischargeOperationtoClientEstimatedDate,
//            tradeRequestBean.finalQualityReporttoClientCompany,tradeRequestBean.finalQualityReporttoClientCost,
//            tradeRequestBean.finalQualityReporttoClientEstimatedDate,tradeRequestBean.finalWeightReporttoClientCompany,
//            tradeRequestBean.finalWeightReporttoClientCost,tradeRequestBean.finalWeightReporttoClientEstimatedDate,
//            tradeRequestBean.tradeOffchainID,IssignedBySeller,IssignedByBuyer,IssignedBySurveyor,preFinancePrice,
//            currentDateTime,listOfListeners,
//
//            tradeRequestBean.notApplicableForFinalQualityReporttoClient).returnValue.get()
//        return ResponseEntity(result, HttpStatus.CREATED)
//
//    }

    @PostMapping(value = "/createTrade", produces = arrayOf("application/json"))
    private fun signedTradeByTrader(@RequestParam tradeId:String,@RequestParam buyer:String):String{
        val owner = proxy.nodeInfo().legalIdentities.get(0)
        println(owner)
      //  val Manufacturer = proxy.partiesFromName(Manufacturer, false).iterator().next()
            ?:throw  IllegalArgumentException("No exact match found for Buyer name.")

        val surveyor = proxy.partiesFromName("BankofAmerica", false).iterator().next()
            ?:throw  IllegalArgumentException("No exact match found for Surveyor name.")
        println(surveyor)
//        val listOfListeners = ArrayList<AbstractParty>()
//        listOfListeners.add(owner)
//        //listOfListeners.add(buyer)
//        listOfListeners.add(surveyor)
//        val result = proxy.startFlowDynamic(CarIssueInitiator::class.java, tradeId, buyer, surveyor).returnValue.get()
//

        return "Trade Signed Successfully By Seller"
    }
}


