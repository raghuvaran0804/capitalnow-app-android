package com.capitalnowapp.mobile.models.offerModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CSActiveLoanReq : Serializable {

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null
}