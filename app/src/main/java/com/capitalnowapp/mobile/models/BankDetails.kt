package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BankDetails : Serializable {

    @SerializedName("bank_code")
    @Expose
    var bankCode: String? = null

    @SerializedName("bank_name")
    @Expose
    var bankName: String? = null
}