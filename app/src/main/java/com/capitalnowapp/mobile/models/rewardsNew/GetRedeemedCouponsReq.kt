package com.capitalnowapp.mobile.models.rewardsNew

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetRedeemedCouponsReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

}
