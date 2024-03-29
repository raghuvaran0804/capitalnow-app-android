package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class EligibleOfferDetailsLoanSummery : Serializable {

    @SerializedName("loan_amount_text")
    @Expose
    var loanAmountText: String? = null

    @SerializedName("loan_amount")
    @Expose
    var loanAmount: String? = null

    @SerializedName("processing_fee_text")
    @Expose
    var processingFeeText: String? = null

    @SerializedName("processing_fee_amount")
    @Expose
    var processingFeeAmount: String? = null

    @SerializedName("gst_text")
    @Expose
    var gstText: String? = null

    @SerializedName("gst_amount")
    @Expose
    var gstAmount: String? = null

    @SerializedName("insurance_text")
    @Expose
    var insuranceText: String? = null

    @SerializedName("insurance_amount")
    @Expose
    var insuranceAmount: String? = null

    @SerializedName("disbursal_text")
    @Expose
    var disbursalText: String? = null

    @SerializedName("disbursal_amount")
    @Expose
    var disbursalAmount: String? = null

    @SerializedName("isInsurance")
    @Expose
    var isInsurance: Boolean? = null

}
