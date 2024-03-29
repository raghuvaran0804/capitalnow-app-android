package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ApplyLoanServiceDataReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("current_screen")
    @Expose
    var currentScreen: String? = null

    @SerializedName("device_unique_id")
    @Expose
    var deviceUniqueId: String? = null

}
