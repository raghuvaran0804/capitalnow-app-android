package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Loandetails {
    @SerializedName("loan_fee")
    @Expose
    var loanFee: String? = null

    @SerializedName("loan_interest")
    @Expose
    var loanInterest: String? = null

    @SerializedName("loan_amount")
    @Expose
    var loanAmount: String? = null

    @SerializedName("cashback")
    @Expose
    var cashback: Int? = null
}