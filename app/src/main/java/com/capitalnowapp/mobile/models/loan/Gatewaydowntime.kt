package com.capitalnowapp.mobile.models.loan

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Gatewaydowntime {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("is_upi")
    @Expose
    var isUpi: Boolean? = null

    @SerializedName("is_bank")
    @Expose
    var isBank: Boolean? = null

    @SerializedName("upi_array")
    @Expose
    var upiArray: List<UpiArray>? = null

    @SerializedName("bank_array")
    @Expose
    var bankArray: BankArray? = null
}