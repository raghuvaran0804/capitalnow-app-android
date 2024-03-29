package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class RegisterLoanReq : Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null
    @SerializedName("amount")
    @Expose
    var amount: String? = null

    @SerializedName("refercode")
    @Expose
    var refercode: String? = null

    @SerializedName("purpose_of_loan")
    @Expose
    var purposeOfLoan: String? = null
}
