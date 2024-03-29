package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TwlLoanDetailsReq :Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("twl_id")
    @Expose
    var twlId: Int? = null
}