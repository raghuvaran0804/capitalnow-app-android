package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SavePrimaryMandateReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("cem_id")
    @Expose
    var cemId: String? = null
}