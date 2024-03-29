package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SaveLoanAmountRequiredReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("loan_amount")
    @Expose
    var loanAmount: String? = null

    @SerializedName("page_no")
    @Expose
    var pageNo: Int? = null
}