package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CSInstallment : Serializable{

    @SerializedName("interest")
    @Expose
    var interest: Int? = null

    @SerializedName("principal")
    @Expose
    var principal: Int? = null

    @SerializedName("instalment")
    @Expose
    var instalment: Int? = null

    @SerializedName("instalmentNo")
    @Expose
    var instalmentNo: Int? = null

    @SerializedName("outstandingPrincipal")
    @Expose
    var outstandingPrincipal: Int? = null

}
