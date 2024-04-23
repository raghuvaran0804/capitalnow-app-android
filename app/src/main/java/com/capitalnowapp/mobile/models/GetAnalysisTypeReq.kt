package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetAnalysisTypeReq : Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("bank_code")
    @Expose
    var bankCode: String? = null

    @SerializedName("referrer")
    @Expose
    var referrer: String? = null
}