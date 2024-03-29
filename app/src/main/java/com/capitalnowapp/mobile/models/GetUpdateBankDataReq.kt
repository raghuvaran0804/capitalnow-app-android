package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetUpdateBankDataReq: Serializable {

    @SerializedName("bank_account_num")
    @Expose
    var bankAccountNumber: String? = null

    @SerializedName("ifsc_code")
    @Expose
    var ifscCode: String? = null

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("token")
    @Expose
    private var usertoken: String? = null


}