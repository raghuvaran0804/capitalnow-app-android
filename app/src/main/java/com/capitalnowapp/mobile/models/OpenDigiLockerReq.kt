package com.capitalnowapp.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OpenDigiLockerReq : Serializable{

    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

}
