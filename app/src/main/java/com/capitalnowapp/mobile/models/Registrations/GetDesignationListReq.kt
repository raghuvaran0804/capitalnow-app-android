package com.capitalnowapp.mobile.models.Registrations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetDesignationListReq : Serializable{
    @SerializedName("api_key")
    @Expose
    var apiKey: String? = null

    @SerializedName("request_input")
    @Expose
    var requestInput: String? = null

}
