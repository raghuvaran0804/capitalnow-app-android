package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class KfsData :Serializable{
    @SerializedName("loan_amount")
    @Expose
    var loanAmount: String? = null

    @SerializedName("rate_of_interest")
    @Expose
    var rateOfInterest: String? = null

    @SerializedName("processing_fee")
    @Expose
    var processingFee: String? = null

    @SerializedName("tenure")
    @Expose
    var tenure: String? = null

    @SerializedName("apr")
    @Expose
    var apr: String? = null

    @SerializedName("emi_amount")
    @Expose
    var emiAmount: String? = null

}
