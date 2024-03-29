package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CancelTwlLoanReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("twl_id")
    @Expose
    var twlId: Int? = null

    @SerializedName("cancel_rid")
    @Expose
    var cancelRid: Int? = null
}