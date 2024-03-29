package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetEligibleOfferDetailReq : Serializable {
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("promocode")
    @Expose
    var promoCode: String? = null

    @SerializedName("uct_id")
    @Expose
    var uctId: String? = null
}
