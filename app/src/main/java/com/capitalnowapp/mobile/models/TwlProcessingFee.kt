package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TwlProcessingFee :Serializable {

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("req_loan_id")
    @Expose
    var reqLoanId: Int? = null

    @SerializedName("price_breakup")
    @Expose
    var priceBreakup: List<String>? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("why_downpayment")
    @Expose
    var whyDownpayment: String? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null
}