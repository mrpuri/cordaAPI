package com.template.Utilities

import com.template.beans.CarRequestResponseBean
import com.template.beans.carRequestBean
import com.template.states.CarState
import net.corda.core.contracts.StateAndRef
import java.util.ArrayList

class CommonUtilities {
    companion object {
        fun convertToCarResponseBean(list: List<StateAndRef<CarState>>): List<CarRequestResponseBean>{
            val CarResponseBean = ArrayList<CarRequestResponseBean>()
            for (i in 0 until list.size) {
                val carRequestBean = list.get(i).state.data
//
                var res = CarRequestResponseBean(carRequestBean.linearId, carRequestBean.vin, carRequestBean.licensePlateNumber, carRequestBean.make, carRequestBean.model, carRequestBean.dealershipLocation)
                var LinearId= carRequestBean.linearId
                CarResponseBean.add(res)


            }
            return CarResponseBean
        }

    }
}
